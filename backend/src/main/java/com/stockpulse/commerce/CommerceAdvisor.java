package com.stockpulse.commerce;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;

public interface CommerceAdvisor {
    
    PricingRecommendation generatePricingRecommendation(Product product, TriggerReason triggerReason);
    
    ReorderRecommendation generateReorderRecommendation(Product product, TriggerReason triggerReason);
}