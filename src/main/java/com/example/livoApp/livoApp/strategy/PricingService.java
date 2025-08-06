package com.example.livoApp.livoApp.strategy;


import com.example.livoApp.livoApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {
    public BigDecimal calculateDynamicPrice(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyStrategyPricing(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

       return pricingStrategy.calculatePrice(inventory);

    }
}
