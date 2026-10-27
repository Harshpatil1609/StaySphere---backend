package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.RoomDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Room;
import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.exception.UnAuthorizedException;
import com.harshpatil.projects.airBnbProject.repository.HotelRepository;
import com.harshpatil.projects.airBnbProject.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.harshpatil.projects.airBnbProject.util.AppUtils.getCurrentUser;

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

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+hotelId);
        }

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

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals( room.getHotel().getOwner() ))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+room.getHotel().getId());
        }


        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomOfHotel(Long hotelId) {
        log.info("getting all room in hotel with id : {}",hotelId);

        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("hotel not found with id : "+hotelId));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+hotelId);
        }

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

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals( room.getHotel().getOwner() ))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+room.getHotel().getId());
        }
//        delete the booking associated with room
        inventoryService.deleteFutureInventories(room);
        roomRepository.deleteById(roomId);


        return true;

//        delete all invemty for future
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long roomId, RoomDto roomDto ,Long hotelId) {
        Room room=roomRepository
                .findById(roomId)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Room is not found with id :"+roomId));

        log.info("updating the room with id : {}",roomId);

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel is not found with id :"+hotelId
                        )
                );

        User user=getCurrentUser();
        log.info("Current user : "+user.getName());
        log.info("Owner : "+hotel.getOwner().getName());

        if(!user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+hotelId);
        }

        room=modelMapper.map(roomDto,Room.class);
        room.setId(roomId);
        room.setHotel(hotel);

//      todo if proce or inventory is updated then ipdate the inventory for this room

        room=roomRepository.save(room);
        return modelMapper.map(room,RoomDto.class);
    }
}
