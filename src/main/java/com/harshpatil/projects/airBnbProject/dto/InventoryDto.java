package com.harshpatil.projects.airBnbProject.dto;

import com.harshpatil.projects.airBnbProject.entity.Hotel;
import com.harshpatil.projects.airBnbProject.entity.Room;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryDto {

    private Long id;

    private Integer bookedCount;

    private Integer reservedCount;

    private Integer totalCount;

    private BigDecimal surgeFactor;

    private BigDecimal price;
//  base price *surgefacotr

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean closed;
}
