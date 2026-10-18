package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.dto.HotelInfoDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;



    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating a new hotel with name:  {}",hotelDto.getName());
        Hotel hotelToSave =modelMapper.map(hotelDto,Hotel.class);
        hotelToSave.setActive(false);

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotelToSave.setOwner(user);

        Hotel savedHotel=hotelRepository.save(hotelToSave);
        log.info("created a new hotel with id : {}",savedHotel.getId());
        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting the hotel with id : {}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("hotel not found with id : "+id));

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+id);
        }

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("updating the hotel with id : {}",id);

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel is not found with id :"+id
                        )
                );

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+id);
        }

        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
        hotel =hotelRepository.save(hotel);

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Transactional
    @Override
    public Boolean deleteHotelById(Long id) {

        Hotel hotel=hotelRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("hotel not found with id : "+id)
        );

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+id);
        }

//        delete the future inventory for this hotel
        for(Room room:hotel.getRooms())
        {
            inventoryService.deleteFutureInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);

        return true;
    }

    @Transactional
    @Override
    public void activateHotel(Long id) {
        log.info("activating the hotel with id : {}",id);

        Hotel hotel=hotelRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("hotel not found with id : "+id)
        );

        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(user.equals(hotel.getOwner()))
        {
            throw new UnAuthorizedException("this user does not own this hotel with id : "+id);
        }

        hotel.setActive(true);

        hotelRepository.save(hotel);

//        create invemtory for all the rooms for thos hotel

        log.info("hotel {} active status set to {}",hotel.getId(),hotel.getActive());

        for(Room room:hotel.getRooms())
        {
            inventoryService.intializeRoomForAYear(room);
        }
    }

    @Override
    public List<HotelDto> getAllHotel() {
        List<Hotel> hotelList = hotelRepository.findAll();

        return hotelList
                .stream()
                .map((element) -> modelMapper.map(element, HotelDto.class)).collect(Collectors.toList());

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(
                ()->new ResourceNotFoundException("hotel not found with id : "+hotelId)
        );

        List<RoomDto> rooms= hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }
}
