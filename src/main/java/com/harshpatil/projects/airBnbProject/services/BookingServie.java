package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.*;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingServie {

    BookingDto initalizeBooking(BookingRequestDto bookingRequestDto);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initalizePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelPayment(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelID, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();

    BookingDto updateGuest(Long bookingId, Long guestId, GuestUpdateRequestDto guestUpdateRequestDto);

    void deleteGuest(Long bookingId, Long guestId);
}
