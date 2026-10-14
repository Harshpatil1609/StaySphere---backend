package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.HotelDto;
import com.harshpatil.projects.airBnbProject.dto.HotelPriceDto;
import com.harshpatil.projects.airBnbProject.dto.HotelSearchRequest;
import com.harshpatil.projects.airBnbProject.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


public interface InventoryService {
    void intializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
