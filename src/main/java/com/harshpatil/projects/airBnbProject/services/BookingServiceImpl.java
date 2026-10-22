package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.BookingRequestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestDto;
import com.harshpatil.projects.airBnbProject.entity.*;
import com.harshpatil.projects.airBnbProject.entity.enums.BookingStatus;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.exception.UnAuthorizedException;
import com.harshpatil.projects.airBnbProject.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCancelParams;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import com.stripe.Stripe;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingServie {
    private final GuestRepository guestRepository;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final CheckOutService checkOutService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initalizeBooking(BookingRequestDto bookingRequestDto) {
        log.info("initializing booking for hotel : {} room : {} ,date {} - {} "
                ,bookingRequestDto.getHotelId(),bookingRequestDto.getRoomId(),bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate());

        Hotel hotel=hotelRepository.findById(bookingRequestDto.getHotelId())
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id : "+bookingRequestDto.getHotelId()));

        Room room=roomRepository.findById(bookingRequestDto.getRoomId())
                .orElseThrow(()-> new ResourceNotFoundException("room not found with id : "+bookingRequestDto.getRoomId()));

        List<Inventory> inventoryList =inventoryRepository.findAndLockAvaibleInventory(room.getId(),
                bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate(),bookingRequestDto.getRoomsCount()
                );

        long daysCount= ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount)
        {
            throw new IllegalStateException("room is not avaible anymore");
        }

//        reserve the room / update the booked count of inventories
        inventoryRepository.initBooking(room.getId(),bookingRequestDto.getCheckInDate(),bookingRequestDto.getCheckOutDate(),bookingRequestDto.getRoomsCount());

//        create the booking
//calculate total amount using daily inventory prices

        BigDecimal totalAmount=inventoryList.stream()
                .map(inventory -> inventory.getPrice().multiply(BigDecimal.valueOf(bookingRequestDto.getRoomsCount())))
                .reduce(BigDecimal.ZERO ,BigDecimal::add);

        Booking booking= Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequestDto.getRoomsCount())
                .amount(totalAmount)
                .build();

        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("adding guest for booking with id :{} ",bookingId);

        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking not found with id : "+bookingId));

        if (hasBookingExpired(booking))
        {
            throw new IllegalStateException("booking has already expired");
        }

        User user=getCurrentUser();
//        System.out.println(booking.getUser());
        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorizedException("Booking does not belong to this user with id : "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED)
        {
            throw new IllegalStateException("booking is not under reserved state cannot add guests");
        }

        for(GuestDto guestDto:guestDtoList)
        {
            Guest guest=modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUSETS_ADDED);
        booking =bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initalizePayment(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id : "+bookingId)
        );

        if (hasBookingExpired(booking))
        {
            throw new IllegalStateException("booking has already expired");
        }

        User user=getCurrentUser();

        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorizedException("Booking does not belong to this user with id : "+user.getId());
        }

        String sessionUrl= checkOutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure" );

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;

    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType()))
        {

            log.info("Stripe SDK version: {}", Stripe.VERSION);
            log.info("Stripe SDK API version: {}", Stripe.API_VERSION);
            log.info("Stripe event API version: {}", event.getApiVersion());

            var deserializer = event.getDataObjectDeserializer();

            Session session = (Session) deserializer.getObject()
                    .orElseThrow(() -> new IllegalStateException(
                            "Could not deserialize Checkout Session. " +
                                    "Stripe event API version: " + event.getApiVersion()
                    ));

            log.info("Checkout Session ID: {}", session.getId());
            log.info("Payment status: {}", session.getPaymentStatus());

            if(session != null)
            {
                String sessionId=session.getId();
                Booking booking=bookingRepository.findByPaymentSessiond(sessionId).orElseThrow(() -> new ResourceNotFoundException("Booking not found for session id : "+sessionId));

                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                        booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount() );

                inventoryRepository.confirmBooking(booking.getRoom().getId(),
                        booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

                log.info("Successfully confirmed the booking for booking id : "+booking.getId());

            }
        }
        else if("checkout.session.expired".equals(event.getType()))
        {
            var deserializer = event.getDataObjectDeserializer();

            Session session = (Session) deserializer.getObject()
                    .orElseThrow(() -> new IllegalStateException(
                            "Could not deserialize Checkout Session. " +
                                    "Stripe event API version: " + event.getApiVersion()
                    ));

            if(session !=null)
            {
                String sessionId=session.getId();
                Booking booking=bookingRepository.findByPaymentSessiond(sessionId).orElseThrow(() -> new ResourceNotFoundException("Booking not found for session id : "+sessionId));

                booking.setBookingStatus(BookingStatus.EXPIRED);

                inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                        booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount() );

                inventoryRepository.expiredBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
            }
        }
        else
        {
            log.warn("unhandled event type : {}",event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelPayment(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id : "+bookingId)
        );

        User user=getCurrentUser();

        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorizedException("Booking does not belong to this user with id : "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED)
        {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);


        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount() );

        inventoryRepository.cancelBooking(booking.getRoom().getId(),
                booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount() );

//        handle the refund

        try {
            Session session=Session.retrieve(booking.getPaymentSessiond());

            RefundCreateParams refundCreateParams=RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id : "+bookingId)
        );
        User user=getCurrentUser();

        if(!user.equals(booking.getUser()))
        {
            throw new UnAuthorizedException("Booking does not belong to this user with id : "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

    public boolean hasBookingExpired(Booking booking)
    {
        return booking.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser()
    {

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
