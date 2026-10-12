package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.BookingRequestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestDto;

import java.util.List;

public interface BookingServie {

    BookingDto initalizeBooking(BookingRequestDto bookingRequestDto);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
