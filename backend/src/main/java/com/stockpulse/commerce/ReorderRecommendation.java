package com.stockpulse.commerce;

public class ReorderRecommendation {
    private final Integer recommendedQuantity;
    private final Double confidence;
    private final String reasoning;
    
    public ReorderRecommendation(Integer recommendedQuantity, Double confidence, String reasoning) {
        this.recommendedQuantity = recommendedQuantity;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
    
    public Integer getRecommendedQuantity() {
        return recommendedQuantity;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    @Override
    public String toString() {
        return "ReorderRecommendation{" +
                "recommendedQuantity=" + recommendedQuantity +
                ", confidence=" + confidence +
                ", reasoning='" + reasoning + '\'' +
                '}';
    }
}