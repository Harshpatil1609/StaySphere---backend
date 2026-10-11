package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> creatNewHotel(@RequestBody HotelDto hotelDto)
    {
        log.info("attempting to create new hotel with name : "+hotelDto.getCity());

        HotelDto savedHotel=hotelService.createNewHotel(hotelDto);

        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")

    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id)
    {
        HotelDto hotel=hotelService.getHotelById(id);

        return ResponseEntity.ok(hotel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id,@RequestBody HotelDto hotelDto)
    {
        HotelDto hotel =hotelService.updateHotelById(id, hotelDto);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteHotelById(@PathVariable Long id)
    {
        boolean exists=hotelService.deleteHotelById(id);
        return ResponseEntity.ok(exists);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long id)
    {
        hotelService.activateHotel(id);
        return ResponseEntity.noContent().build();
    }
}
