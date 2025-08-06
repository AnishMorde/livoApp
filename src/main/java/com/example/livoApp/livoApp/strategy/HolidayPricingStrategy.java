package com.example.livoApp.livoApp.strategy;

import com.example.livoApp.livoApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{
    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
       BigDecimal price = wrapped.calculatePrice(inventory);
       boolean isTodayHoliday = true;
       if(isTodayHoliday){
           price.multiply(BigDecimal.valueOf(1.25));
       }

       return price;

    }
}
