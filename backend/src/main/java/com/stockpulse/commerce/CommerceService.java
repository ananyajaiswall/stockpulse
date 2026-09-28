package com.stockpulse.commerce;

import com.stockpulse.product.Product;
import com.stockpulse.product.ProductService;
import com.stockpulse.product.ProductStatus;
import com.stockpulse.pricing.PricingSuggestion;
import com.stockpulse.pricing.PricingSuggestionService;
import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.reorder.ReorderSuggestion;
import com.stockpulse.reorder.ReorderSuggestionService;
import com.stockpulse.common.TriggerReason;
import com.stockpulse.event.InventoryChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommerceService {
    
    private final CommerceAdvisor commerceAdvisor;
    private final ProductService productService;
    private final PricingSuggestionService pricingSuggestionService;
    private final ReorderSuggestionService reorderSuggestionService;
    private final ApplicationEventPublisher eventPublisher;
    
    public CommerceService(CommerceAdvisor commerceAdvisor,
                          ProductService productService,
                          PricingSuggestionService pricingSuggestionService,
                          ReorderSuggestionService reorderSuggestionService,
                          ApplicationEventPublisher eventPublisher) {
        this.commerceAdvisor = commerceAdvisor;
        this.productService = productService;
        this.pricingSuggestionService = pricingSuggestionService;
        this.reorderSuggestionService = reorderSuggestionService;
        this.eventPublisher = eventPublisher;
    }
    
    public void triggerCommerceReview(String productId, TriggerReason triggerReason) {
        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        // Publish event for async processing
        eventPublisher.publishEvent(new InventoryChangedEvent(this, product, triggerReason));
    }
    
    public PricingSuggestion createPricingSuggestion(String productId, TriggerReason triggerReason) {
        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        PricingRecommendation recommendation = commerceAdvisor.generatePricingRecommendation(product, triggerReason);
        
        return pricingSuggestionService.createSuggestion(
            product,
            product.getCurrentPrice(),
            recommendation.getRecommendedPrice(),
            recommendation.getDirection(),
            recommendation.getConfidence(),
            recommendation.getReasoning(),
            triggerReason
        );
    }
    
    public ReorderSuggestion createReorderSuggestion(String productId, TriggerReason triggerReason) {
        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        ReorderRecommendation recommendation = commerceAdvisor.generateReorderRecommendation(product, triggerReason);
        
        return reorderSuggestionService.createSuggestion(
            product,
            product.getStockLevel(),
            recommendation.getRecommendedQuantity(),
            recommendation.getConfidence(),
            recommendation.getReasoning(),
            triggerReason
        );
    }
    
    @Transactional
    public PricingSuggestion acceptPricingSuggestion(String suggestionId) {
        PricingSuggestion suggestion = pricingSuggestionService.updateSuggestionStatus(suggestionId, SuggestionStatus.ACCEPTED);
        
        // Product is managed in this transaction, so changes are flushed on commit
        Product product = suggestion.getProduct();
        product.setCurrentPrice(suggestion.getRecommendedPrice());
        if (product.getStatus() == ProductStatus.PRICE_REVIEW_PENDING) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        return suggestion;
    }
    
    @Transactional
    public ReorderSuggestion acceptReorderSuggestion(String suggestionId) {
        ReorderSuggestion suggestion = reorderSuggestionService.updateSuggestionStatus(suggestionId, SuggestionStatus.ACCEPTED);
        
        // Product is managed in this transaction; increaseStock also clears OUT_OF_STOCK
        suggestion.getProduct().increaseStock(suggestion.getRecommendedQuantity());

        return suggestion;
    }
    
    public PricingSuggestion rejectPricingSuggestion(String suggestionId) {
        return pricingSuggestionService.updateSuggestionStatus(suggestionId, SuggestionStatus.REJECTED);
    }
    
    public ReorderSuggestion rejectReorderSuggestion(String suggestionId) {
        return reorderSuggestionService.updateSuggestionStatus(suggestionId, SuggestionStatus.REJECTED);
    }
}