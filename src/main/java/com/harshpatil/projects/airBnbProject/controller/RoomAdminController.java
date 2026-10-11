package com.harshpatil.projects.airBnbProject.controller;


import com.harshpatil.projects.airBnbProject.dto.RoomDto;
import com.harshpatil.projects.airBnbProject.entity.Room;
import com.harshpatil.projects.airBnbProject.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto)
    {
        RoomDto roomDto1=roomService.createNewRoom(hotelId,roomDto);

        return  new ResponseEntity<>(roomDto1, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsOfHotel(@PathVariable Long hotelId)
    {
        return ResponseEntity.ok(roomService.getAllRoomOfHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId ,@PathVariable Long roomId)
    {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Boolean> deleteRoomById(@PathVariable Long hotelId,@PathVariable Long roomId)
    {
        return ResponseEntity.ok(roomService.deleteRoomById(roomId));
    }
}
