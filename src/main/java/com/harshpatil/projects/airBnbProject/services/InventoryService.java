package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.*;
import com.harshpatil.projects.airBnbProject.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


public interface InventoryService {
    void intializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoomId(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);

    void updateInventoryAfterRoomUpdate(Room room);
}
