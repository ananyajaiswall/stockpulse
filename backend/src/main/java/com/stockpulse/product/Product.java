package com.stockpulse.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    private String id;
    
    @NotBlank
    @Column(unique = true)
    private String sku;
    
    @NotBlank
    private String name;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductCategory category;
    
    @NotNull
    @Column(name = "current_price")
    private BigDecimal currentPrice;
    
    @NotNull
    @Column(name = "stock_level")
    private Integer stockLevel;
    
    @NotNull
    @Column(name = "reorder_threshold")
    private Integer reorderThreshold;
    
    @NotNull
    @Column(name = "demand_velocity")
    private Integer demandVelocity;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductStatus status;
    
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    
    @Column(name = "supplier_id")
    private String supplierId;
    
    // Constructors
    public Product() {}
    
    public Product(String id, String sku, String name, ProductCategory category, 
                  BigDecimal currentPrice, Integer stockLevel, Integer reorderThreshold, 
                  Integer demandVelocity, ProductStatus status) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.currentPrice = currentPrice;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.demandVelocity = demandVelocity;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public Integer getStockLevel() {
        return stockLevel;
    }
    
    public void setStockLevel(Integer stockLevel) {
        this.stockLevel = stockLevel;
        // Update status based on stock level
        if (stockLevel == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }
    
    public Integer getReorderThreshold() {
        return reorderThreshold;
    }
    
    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }
    
    public Integer getDemandVelocity() {
        return demandVelocity;
    }
    
    public void setDemandVelocity(Integer demandVelocity) {
        this.demandVelocity = demandVelocity;
    }
    
    public ProductStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    // Business methods
    public boolean isLowStock() {
        return this.stockLevel < this.reorderThreshold;
    }
    
    public void increaseDemandVelocity(int amount) {
        this.demandVelocity = this.demandVelocity + amount;
    }
    
    public void decreaseStock(int amount) {
        this.stockLevel = this.stockLevel - amount;
        if (this.stockLevel <= 0) {
            this.stockLevel = 0;
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }
    
    public void increaseStock(int amount) {
        this.stockLevel = this.stockLevel + amount;
        if (this.status == ProductStatus.OUT_OF_STOCK && this.stockLevel > 0) {
            this.status = ProductStatus.ACTIVE;
        }
    }
}