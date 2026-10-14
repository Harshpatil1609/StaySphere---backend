package com.harshpatil.projects.airBnbProject.strategy;

import com.harshpatil.projects.airBnbProject.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory)
    {
        PrincingStrategy princingStrategy = new BasePricingStrategy();
//        apply additional strategies
        princingStrategy=new SurgePriceStrategy(princingStrategy);

        princingStrategy=new OccupencyPrincingStrategy(princingStrategy);
        princingStrategy=new UrgencyPriceStrategy(princingStrategy);
        princingStrategy=new HolidayPricingStrategy(princingStrategy);

        return princingStrategy.calculatePrice(inventory);
    }
}
