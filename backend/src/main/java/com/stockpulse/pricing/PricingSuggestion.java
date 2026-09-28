package com.stockpulse.pricing;

import com.stockpulse.product.Product;
import com.stockpulse.common.TriggerReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_suggestions")
public class PricingSuggestion {
    
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;
    
    @Column(name = "current_price")
    @NotNull
    private BigDecimal currentPrice;
    
    @Column(name = "recommended_price")
    @NotNull
    private BigDecimal recommendedPrice;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private PriceDirection direction;
    
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
    public PricingSuggestion() {}
    
    public PricingSuggestion(String id, Product product, BigDecimal currentPrice, 
                           BigDecimal recommendedPrice, PriceDirection direction, 
                           Double confidence, String reasoning, TriggerReason triggerReason) {
        this.id = id;
        this.product = product;
        this.currentPrice = currentPrice;
        this.recommendedPrice = recommendedPrice;
        this.direction = direction;
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
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getRecommendedPrice() {
        return recommendedPrice;
    }
    
    public void setRecommendedPrice(BigDecimal recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }
    
    public PriceDirection getDirection() {
        return direction;
    }
    
    public void setDirection(PriceDirection direction) {
        this.direction = direction;
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