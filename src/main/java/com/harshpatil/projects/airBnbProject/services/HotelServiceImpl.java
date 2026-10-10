package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.HotelRepository;
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

    @Override
    public Boolean deleteHotelById(Long id) {
        boolean exists=hotelRepository.existsById(id);

        if(!exists)
        {
            throw new ResourceNotFoundException("hotel not found with id : "+id);
        }

        hotelRepository.deleteById(id);
//        delete the future inventory for this hotel
        return true;
    }
}
