package com.stockpulse.pricing.mapper;

import com.stockpulse.pricing.PricingSuggestion;
import com.stockpulse.pricing.dto.PricingSuggestionDTO;
import org.springframework.stereotype.Component;

@Component
public class PricingSuggestionMapper {
    
    public PricingSuggestionDTO toDTO(PricingSuggestion suggestion) {
        if (suggestion == null) {
            return null;
        }
        
        return new PricingSuggestionDTO(
            suggestion.getId(),
            suggestion.getProduct().getId(),
            suggestion.getProduct().getName(),
            suggestion.getCurrentPrice(),
            suggestion.getRecommendedPrice(),
            suggestion.getDirection(),
            suggestion.getConfidence(),
            suggestion.getReasoning(),
            suggestion.getStatus(),
            suggestion.getTriggerReason(),
            suggestion.getCreatedAt()
        );
    }
}