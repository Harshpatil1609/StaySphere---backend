package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Inventory;
import com.harshpatil.projects.airBnbProject.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    @Query("""
        SELECT DISTINCT i.hotel
        FROM Inventory i
        WHERE i.city=:city
        AND i.date BETWEEN :startDate AND :endDate
        AND i.closed =false
        AND (i.totalCount-i.bookedCount - i.reservedCount) >= :roomsCount
                GROUP BY i.hotel ,i.room
                        HAVING COUNT(i.date)=:dateCount
        """
    )
    Page<Hotel> findHotelsWithAvaibleInventory(
            @Param("city") String city,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
            );
    @Query(
            """
        SELECT i
        FROM Inventory i
        WHERE i.room.id=:roomId
        AND i.date BETWEEN :startDate AND :endDate
        AND i.closed =false
        AND (i.totalCount-i.bookedCount - i.reservedCount) >= :roomsCount
    """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvaibleInventory(
            @Param("roomId") Long roomId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel,  LocalDate startDate, LocalDate endDate);
}
