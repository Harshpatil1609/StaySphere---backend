package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UrgencyPriceStrategy implements PrincingStrategy{
    private final PrincingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);

        LocalDate today= LocalDate.now();

        if(!inventory.getDate().isBefore(today) && inventory.getDate().isBefore(today.plusDays(7)))
        {
            price=price.multiply(BigDecimal.valueOf(1.15));
        }

        return  price;
    }
}
