package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.dto.*;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Inventory;
import com.harshpatil.projects.airBnbProject.entity.Room;
import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.exception.ResourceNotFoundException;
import com.harshpatil.projects.airBnbProject.repository.HotelMinPriceRepository;
import com.harshpatil.projects.airBnbProject.repository.InventoryRepository;
import com.harshpatil.projects.airBnbProject.repository.RoomRepository;
import com.harshpatil.projects.airBnbProject.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.harshpatil.projects.airBnbProject.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final RoomRepository roomRepository;

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Override
    public void intializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);

        for (;!today.isAfter(endDate); today=today.plusDays(1))
        {
            Inventory inventory=Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);

        }
    }

    @Override
    public void deleteFutureInventories(Room room) {
        log.info("deleting the inventory of room with id : {}",room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        log.info("Searching hotels for {} city , from {} to {}",hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());

        Pageable pageable= PageRequest.of(hotelSearchRequest.getPage(),hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

        Page<HotelPriceDto> hotelPage=  hotelMinPriceRepository.findHotelsWithAvaibleInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomsCount(),dateCount,pageable);

        return hotelPage;
    }

    @Override
    @Transactional
    public List<InventoryDto> getAllInventoryByRoomId(Long roomId) {

        log.info("Getting all inventory for room with id : ",roomId);
        Room room =roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with id : "+roomId));

        User user=getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
        {
            throw new AccessDeniedException("you are not the owner of room with id : "+roomId);
        }

        List<Inventory> inventoryList=inventoryRepository.findByRoomOrderByDate(room);

        return inventoryList
                .stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Update all inventory for room with id : ",roomId);
        Room room =roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with id : "+roomId));

        User user=getCurrentUser();

        if(!user.equals(room.getHotel().getOwner()))
        {
            throw new AccessDeniedException("you are not the owner of room with id : "+roomId);
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getSurgeFactor(),updateInventoryRequestDto.getClosed());
    }

    @Override
    @Transactional
    public void updateInventoryAfterRoomUpdate(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);

        List<Inventory> inventoryList=inventoryRepository.getInventoryAndLockBeforeUpdate(room.getId(),today,endDate);

        for (Inventory inventory:inventoryList)
        {
            int committedRooms=inventory.getBookedCount()+inventory.getReservedCount();

            if(room.getTotalCount() < committedRooms)
            {
                throw new IllegalStateException("Cannot reduce room capacity bellow booked and reserved rooms for date : "+inventory.getDate());

            }

            inventory.setTotalCount(room.getTotalCount());

            inventory.setPrice(pricingService.calculateDynamicPricing(inventory));

            inventoryRepository.saveAll(inventoryList);
        }
    }
}
