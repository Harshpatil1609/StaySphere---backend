package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Room;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating a new hotel with name:  {}",hotelDto.getName());
        Hotel hotelToSave =modelMapper.map(hotelDto,Hotel.class);
        hotelToSave.setActive(false);
        Hotel savedHotel=hotelRepository.save(hotelToSave);
        log.info("created a new hotel with id : {}",savedHotel.getId());
        return modelMapper.map(savedHotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting the hotel with id : {}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("hotel not found with id : "+id));

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

        hotelRepository.deleteById(id);
//        delete the future inventory for this hotel
        for(Room room:hotel.getRooms())
        {
            inventoryService.deleteFutureInventories(room);
        }

        return true;
    }

    @Transactional
    @Override
    public void activateHotel(Long id) {
        log.info("activating the hotel with id : {}",id);

        Hotel hotel=hotelRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("hotel not found with id : "+id)
        );

        hotel.setActive(true);

        hotelRepository.save(hotel);

//        create invemtory for all the rooms for thos hotel

        log.info("hotel {} active status set to {}",hotel.getId(),hotel.getActive());

        for(Room room:hotel.getRooms())
        {
            inventoryService.intializeRoomForAYear(room);
        }
    }
}
