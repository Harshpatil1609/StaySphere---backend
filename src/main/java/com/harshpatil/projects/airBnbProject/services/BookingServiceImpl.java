package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.BookingRequestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestDto;
import com.harshpatil.projects.airBnbProject.entity.*;
import com.harshpatil.projects.airBnbProject.entity.enums.BookingStatus;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        for(Inventory inventory:inventoryList)
        {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);


//        create the booking


//        todo calculate dynamic amout

        Booking booking= Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequestDto.getRoomsCount())
                .amount(BigDecimal.TEN)
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

    public boolean hasBookingExpired(Booking booking)
    {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser()
    {
        User user=new User();
        user.setId(1L);
        return user;
    }
}
