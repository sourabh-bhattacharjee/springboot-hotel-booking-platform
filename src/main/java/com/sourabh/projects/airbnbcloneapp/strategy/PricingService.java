package com.sourabh.projects.airbnbcloneapp.strategy;

import com.sourabh.projects.airbnbcloneapp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //apply additional strategies

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy  = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);

    }
}
