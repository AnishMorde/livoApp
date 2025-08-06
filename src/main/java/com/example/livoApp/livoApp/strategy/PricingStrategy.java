package com.example.livoApp.livoApp.strategy;

import com.example.livoApp.livoApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface PricingStrategy {
    public BigDecimal calculatePrice(Inventory inventory);
}
