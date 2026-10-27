package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.dto.ProfileUpdateRequestDto;
import com.harshpatil.projects.airBnbProject.dto.UserProfile;
import com.harshpatil.projects.airBnbProject.entity.Booking;
import com.harshpatil.projects.airBnbProject.services.BookingServie;
import com.harshpatil.projects.airBnbProject.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingServie bookingServie;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto)
    {
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mybookings")
    public ResponseEntity<List<BookingDto>> getMyBookings()
    {
        return ResponseEntity.ok(bookingServie.getMyBookings());
    }

    @GetMapping
    public ResponseEntity<UserProfile> getMyProfile()
    {

        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PatchMapping("/guests/{guestId}")

}
