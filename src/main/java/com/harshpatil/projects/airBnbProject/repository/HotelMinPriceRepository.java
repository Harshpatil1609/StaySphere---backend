package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.dto.HotelPriceDto;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.HotelMiniPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMiniPrice ,Long> {
    @Query("""
        SELECT new com.harshpatil.projects.airBnbProject.dto.HotelPriceDto(i.hotel,AVG(i.price))
        FROM HotelMiniPrice i
        WHERE i.hotel.city=:city
        AND i.date BETWEEN :startDate AND :endDate
        AND i.hotel.active =true
                GROUP BY i.hotel 
        """
    )
    Page<HotelPriceDto>  findHotelsWithAvaibleInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<HotelMiniPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
