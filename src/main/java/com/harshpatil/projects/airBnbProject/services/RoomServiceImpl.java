package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.RoomDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Room;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.HotelRepository;
import com.harshpatil.projects.airBnbProject.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId ,RoomDto roomDto) {
        log.info("creating new Room in hotel with id : {}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("hotel not found with id "+hotelId));

        Room room=modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        room =roomRepository.save(room);

//        creat inventry as soon as room is created and if hotel is active
        if(hotel.getActive())
        {
            inventoryService.intializeRoomForAYear(room);
        }

        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("getting room with id {}",roomId);
        Room room=roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("room not found with id : "+roomId));

        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomOfHotel(Long hotelId) {
        log.info("getting all room in hotel with id : {}",hotelId);

        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("hotel not found with id : "+hotelId));
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean deleteRoomById(Long roomId) {
        log.info("deleting the room with Id :{}",roomId);

        Room room=roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("room not found with id : "+roomId));

        inventoryService.deleteFutureInventories(room);
        roomRepository.deleteById(roomId);

        return true;

//        delete all invemty for future
    }
}
