package com.harshpatil.projects.airBnbProject.dto;

import com.harshpatil.projects.airBnbProject.entity.HotelContactInfo;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class HotelDto {

    private Long id;

    private String name;

    private String city;
    private String[] photos;

    private String[] amenities;

    private HotelContactInfo contactInfo;

    private Boolean active;
}
