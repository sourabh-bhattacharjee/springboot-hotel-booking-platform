package com.sourabh.projects.airbnbcloneapp.strategy;

import com.sourabh.projects.airbnbcloneapp.entity.Inventory;

import java.math.BigDecimal;


public class BasePricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
