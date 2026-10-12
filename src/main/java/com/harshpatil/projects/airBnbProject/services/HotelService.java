package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.dto.HotelInfoDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    Boolean deleteHotelById(Long id);

    void activateHotel(Long id);

    List<HotelDto> getAllHotel();

    HotelInfoDto getHotelInfoById(Long hotelId);
}
