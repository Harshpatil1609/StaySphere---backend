package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);
}
