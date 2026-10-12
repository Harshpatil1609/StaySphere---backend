package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.BookingRequestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestDto;
import com.harshpatil.projects.airBnbProject.services.BookingServie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingServie bookingServie;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequestDto)
    {
        return ResponseEntity.ok(bookingServie.initalizeBooking(bookingRequestDto));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
        @RequestBody List<GuestDto> guestDtoList
    )
    {
        return ResponseEntity.ok(bookingServie.addGuests(bookingId,guestDtoList));
    }
}
