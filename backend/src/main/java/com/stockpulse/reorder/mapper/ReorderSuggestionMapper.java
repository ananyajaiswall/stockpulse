package com.stockpulse.reorder.mapper;

import com.stockpulse.reorder.ReorderSuggestion;
import com.stockpulse.reorder.dto.ReorderSuggestionDTO;
import org.springframework.stereotype.Component;

@Component
public class ReorderSuggestionMapper {
    
    public ReorderSuggestionDTO toDTO(ReorderSuggestion suggestion) {
        if (suggestion == null) {
            return null;
        }
        
        return new ReorderSuggestionDTO(
            suggestion.getId(),
            suggestion.getProduct().getId(),
            suggestion.getProduct().getName(),
            suggestion.getCurrentStock(),
            suggestion.getRecommendedQuantity(),
            suggestion.getSuggestedLeadTimeDays(),
            suggestion.getConfidence(),
            suggestion.getReasoning(),
            suggestion.getStatus(),
            suggestion.getTriggerReason(),
            suggestion.getCreatedAt()
        );
    }
}