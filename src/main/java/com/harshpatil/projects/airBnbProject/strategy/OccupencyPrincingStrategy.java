package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class OccupencyPrincingStrategy implements PrincingStrategy {

    private final PrincingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price =  wrapped.calculatePrice(inventory);

        double occupancyRate=(double) inventory.getBookedCount()/inventory.getTotalCount();

        if(occupancyRate >0.8)
        {
            price=price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
