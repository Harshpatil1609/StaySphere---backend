package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;

import java.math.BigDecimal;

public interface PrincingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
