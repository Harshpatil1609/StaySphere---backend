package com.harshpatil.projects.airBnbProject.dto;

import lombok.Data;
import java.math.BigDecimal;


@Data
public class RoomDto {

    private Long id;

    private String type;

    private BigDecimal basePrice;

    private String[] photos;

    private String[] amenitites;

    private Integer totalCount;

    private Integer capacity;
}
