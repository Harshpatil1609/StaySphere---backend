package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

}
