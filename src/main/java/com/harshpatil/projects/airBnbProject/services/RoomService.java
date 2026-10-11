package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.RoomDto;
import com.harshpatil.projects.airBnbProject.entity.Room;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);

    RoomDto getRoomById(Long roomId);

    List<RoomDto> getAllRoomOfHotel(Long hotelId);

    Boolean deleteRoomById(Long roomId);
}
