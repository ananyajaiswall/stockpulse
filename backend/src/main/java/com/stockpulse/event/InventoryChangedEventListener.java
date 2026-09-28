package com.stockpulse.event;

import com.stockpulse.commerce.CommerceAdvisor;
import com.stockpulse.commerce.PricingRecommendation;
import com.stockpulse.commerce.ReorderRecommendation;
import com.stockpulse.pricing.PricingSuggestion;
import com.stockpulse.pricing.PricingSuggestionService;
import com.stockpulse.reorder.ReorderSuggestion;
import com.stockpulse.reorder.ReorderSuggestionService;
import com.stockpulse.common.TriggerReason;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class InventoryChangedEventListener {
    
    private final CommerceAdvisor commerceAdvisor;
    private final PricingSuggestionService pricingSuggestionService;
    private final ReorderSuggestionService reorderSuggestionService;
    
    public InventoryChangedEventListener(CommerceAdvisor commerceAdvisor,
                                       PricingSuggestionService pricingSuggestionService,
                                       ReorderSuggestionService reorderSuggestionService) {
        this.commerceAdvisor = commerceAdvisor;
        this.pricingSuggestionService = pricingSuggestionService;
        this.reorderSuggestionService = reorderSuggestionService;
    }
    
    @Async
    @EventListener
    public void handleInventoryChangedEvent(InventoryChangedEvent event) {
        // Check if there's already a pending suggestion for this product and trigger
        boolean hasPendingPricingSuggestion = pricingSuggestionService
            .findPendingByProductAndTrigger(event.getProduct(), event.getTriggerReason())
            .isPresent();
        
        boolean hasPendingReorderSuggestion = reorderSuggestionService
            .findPendingByProductAndTrigger(event.getProduct(), event.getTriggerReason())
            .isPresent();
        
        // Skip if suggestions already exist
        if (hasPendingPricingSuggestion && hasPendingReorderSuggestion) {
            return;
        }
        
        // Generate recommendations
        PricingRecommendation pricingRec = commerceAdvisor.generatePricingRecommendation(
            event.getProduct(), event.getTriggerReason());
        
        ReorderRecommendation reorderRec = commerceAdvisor.generateReorderRecommendation(
            event.getProduct(), event.getTriggerReason());
        
        // Create pricing suggestion if none exists
        if (!hasPendingPricingSuggestion) {
            pricingSuggestionService.createSuggestion(
                event.getProduct(),
                event.getProduct().getCurrentPrice(),
                pricingRec.getRecommendedPrice(),
                pricingRec.getDirection(),
                pricingRec.getConfidence(),
                pricingRec.getReasoning(),
                event.getTriggerReason()
            );
        }
        
        // Create reorder suggestion if none exists
        if (!hasPendingReorderSuggestion) {
            reorderSuggestionService.createSuggestion(
                event.getProduct(),
                event.getProduct().getStockLevel(),
                reorderRec.getRecommendedQuantity(),
                reorderRec.getConfidence(),
                reorderRec.getReasoning(),
                event.getTriggerReason()
            );
        }
    }
}