package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.BookingRequestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestDto;
import com.harshpatil.projects.airBnbProject.dto.GuestUpdateRequestDto;
import com.harshpatil.projects.airBnbProject.entity.Booking;
import com.harshpatil.projects.airBnbProject.services.BookingServie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String ,String>> initiatePayment(@PathVariable Long bookingId)
    {
        String sessionUrl=bookingServie.initalizePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId)
    {
        bookingServie.cancelPayment(bookingId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String ,String>> getBookingStatus(@PathVariable Long bookingId)
    {

        return ResponseEntity.ok(Map.of("status : ",bookingServie.getBookingStatus(bookingId)));
    }

    @PutMapping("/{bookingId}/guests/{guestId}")
    public ResponseEntity<BookingDto> updateGuest(@PathVariable Long bookingId,
                                                  @PathVariable Long guestId,
                                                  @RequestBody GuestUpdateRequestDto guestUpdateRequestDto)
    {
        return ResponseEntity.ok(bookingServie.updateGuest(bookingId,guestId,guestUpdateRequestDto));
    }

    @DeleteMapping("/{bookingId}/guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long bookingId,
                                            @PathVariable Long guestId)
    {
        bookingServie.deleteGuest(bookingId,guestId);
        return ResponseEntity.noContent().build();
    }

}
