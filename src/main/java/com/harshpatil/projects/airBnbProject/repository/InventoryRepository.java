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
import org.springframework.data.jpa.repository.Modifying;
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

//    lock the reserved booking to make changes by the following method
//    this is done to remove the redentancy and concurrent access

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount)>= :roomsCount
                AND i.closed=false
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );


//modifing the inventory on which lock is applied to make change in booking and reserved count
//    does not aquire lock on modifying query
    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.reservedCount = i.reservedCount- :roomsCount,
        i.bookedCount = i.bookedCount+ :roomsCount
        WHERE i.room.id = :roomId
        AND i.date BETWEEN :startDate AND :endDate
        AND (i.totalCount - i.bookedCount)>= :roomsCount
        AND i.closed=false
        """)
    void confirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET  i.bookedCount = i.bookedCount - :roomsCount
        WHERE i.room.id = :roomId
        AND i.date BETWEEN :startDate AND :endDate
        AND (i.bookedCount) >= :roomsCount
        AND i.closed=false
        """)
    void cancelBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET  i.reservedCount = i.reservedCount + :roomsCount
        WHERE i.room.id = :roomId
        AND i.date BETWEEN :startDate AND :endDate
        AND (i.totalCount-i.bookedCount - i.reservedCount) >= :roomsCount
        AND i.closed=false
        """)
    void initBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET  i.reservedCount = i.reservedCount - :roomsCount
        WHERE i.room.id = :roomId
        AND i.date BETWEEN :startDate AND :endDate
        AND i.reservedCount >= :roomsCount
        AND i.closed=false
        """)
    void expiredBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );



    List<Inventory> findByHotelAndDateBetween(Hotel hotel,  LocalDate startDate, LocalDate endDate);
}
