package com.stockpulse.commerce;

import com.stockpulse.pricing.PriceDirection;

import java.math.BigDecimal;

public class PricingRecommendation {
    private final BigDecimal recommendedPrice;
    private final PriceDirection direction;
    private final Double confidence;
    private final String reasoning;
    
    public PricingRecommendation(BigDecimal recommendedPrice, PriceDirection direction, 
                               Double confidence, String reasoning) {
        this.recommendedPrice = recommendedPrice;
        this.direction = direction;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
    
    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }
    
    public PriceDirection getDirection() {
        return direction;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    @Override
    public String toString() {
        return "PricingRecommendation{" +
                "recommendedPrice=" + recommendedPrice +
                ", direction=" + direction +
                ", confidence=" + confidence +
                ", reasoning='" + reasoning + '\'' +
                '}';
    }
}