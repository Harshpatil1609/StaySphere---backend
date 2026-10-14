package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class SurgePriceStrategy implements PrincingStrategy{

    private final PrincingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return wrapped.calculatePrice(inventory).multiply(inventory.getSurgeFactor());
    }
}
