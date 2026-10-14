package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.HotelMiniPrice;
import com.harshpatil.projects.airBnbProject.entity.Inventory;
import com.harshpatil.projects.airBnbProject.repository.HotelMinPriceRepository;
import com.harshpatil.projects.airBnbProject.repository.HotelRepository;
import com.harshpatil.projects.airBnbProject.repository.InventoryRepository;
import com.harshpatil.projects.airBnbProject.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

//    scheduler to update the inventory and hotelmin price table every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * * ")
    public void updatePrices()
    {
        int page=0;
        int batchSize=100;

        while (true)
        {
            Page<Hotel> hotelPage=hotelRepository.findAll(PageRequest.of(page,batchSize));
            if(hotelPage.isEmpty())
            {
                break;
            }

//            update the price of each hotel
            hotelPage.getContent().forEach(hotel -> updateHotelPrices(hotel));

            page++;
        }
    }

    private void updateHotelPrices(Hotel hotel)
    {
        log.info("updating hotel prices for hotel id : {}",hotel.getId());
        LocalDate startDate= LocalDate.now();
        LocalDate endDate=LocalDate.now().plusYears(1);

        List<Inventory> inventoryList=inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);

        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel,inventoryList,startDate,endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate ,BigDecimal> dailyMinPrice=inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice,Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey , e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMiniPrice> hotelPrices=new ArrayList<>();
        dailyMinPrice.forEach((date ,price)->{
            HotelMiniPrice hotelPrice=hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMiniPrice(hotel,date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList)
    {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice=pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryList);
    }

}
