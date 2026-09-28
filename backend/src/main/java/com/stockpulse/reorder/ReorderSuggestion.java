package com.stockpulse.reorder;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import com.stockpulse.pricing.SuggestionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "reorder_suggestions")
public class ReorderSuggestion {
    
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;
    
    @Column(name = "current_stock")
    @NotNull
    private Integer currentStock;
    
    @Column(name = "recommended_quantity")
    @NotNull
    private Integer recommendedQuantity;
    
    @Column(name = "suggested_lead_time_days")
    private Integer suggestedLeadTimeDays;
    
    @NotNull
    private Double confidence;
    
    @NotNull
    private String reasoning;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private SuggestionStatus status;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "trigger_reason")
    private TriggerReason triggerReason;
    
    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;
    
    // Constructors
    public ReorderSuggestion() {}
    
    public ReorderSuggestion(String id, Product product, Integer currentStock, 
                           Integer recommendedQuantity, Double confidence, 
                           String reasoning, TriggerReason triggerReason) {
        this.id = id;
        this.product = product;
        this.currentStock = currentStock;
        this.recommendedQuantity = recommendedQuantity;
        this.confidence = confidence;
        this.reasoning = reasoning;
        this.triggerReason = triggerReason;
        this.status = SuggestionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public Integer getRecommendedQuantity() {
        return recommendedQuantity;
    }
    
    public void setRecommendedQuantity(Integer recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }
    
    public Integer getSuggestedLeadTimeDays() {
        return suggestedLeadTimeDays;
    }
    
    public void setSuggestedLeadTimeDays(Integer suggestedLeadTimeDays) {
        this.suggestedLeadTimeDays = suggestedLeadTimeDays;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
    
    public SuggestionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }
    
    public TriggerReason getTriggerReason() {
        return triggerReason;
    }
    
    public void setTriggerReason(TriggerReason triggerReason) {
        this.triggerReason = triggerReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business methods
    public boolean isPending() {
        return this.status == SuggestionStatus.PENDING;
    }
    
    public void accept() {
        if (this.status != SuggestionStatus.PENDING) {
            throw new IllegalStateException("Only pending suggestions can be accepted");
        }
        this.status = SuggestionStatus.ACCEPTED;
    }
    
    public void reject() {
        if (this.status != SuggestionStatus.PENDING) {
            throw new IllegalStateException("Only pending suggestions can be rejected");
        }
        this.status = SuggestionStatus.REJECTED;
    }
}