package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.entity.Room;
import org.springframework.stereotype.Service;


public interface InventoryService {
    void intializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);
}
