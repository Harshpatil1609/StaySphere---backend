package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;

@RequiredArgsConstructor
public class BasePricingStrategy implements PrincingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
