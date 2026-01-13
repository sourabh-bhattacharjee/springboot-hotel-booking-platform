package com.sourabh.projects.airbnbcloneapp.strategy;

import com.sourabh.projects.airbnbcloneapp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
