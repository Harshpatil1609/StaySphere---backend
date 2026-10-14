package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PrincingStrategy{

    private final PrincingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);

        boolean isTodayHoliday =true;

        if(isTodayHoliday)
        {
            price=price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
