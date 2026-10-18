package com.harshpatil.projects.airBnbProject.repository;

import com.harshpatil.projects.airBnbProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User ,Long> {

    Optional<User> findByEmail(String email);
}
