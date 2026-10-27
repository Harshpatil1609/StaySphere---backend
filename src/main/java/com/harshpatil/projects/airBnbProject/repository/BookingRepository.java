package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.dto.BookingDto;
import com.harshpatil.projects.airBnbProject.entity.Booking;
import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<Booking> findByPaymentSessiond(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDate,LocalDateTime endDate);

    List<Booking> findByUser(User user);
}
