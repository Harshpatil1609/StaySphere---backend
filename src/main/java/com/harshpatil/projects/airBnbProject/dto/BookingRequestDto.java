package com.harshpatil.projects.airBnbProject.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;
}
