package com.stockpulse.product.dto;

import com.stockpulse.product.ProductCategory;
import com.stockpulse.product.ProductStatus;

import java.math.BigDecimal;

public record CreateProductDTO(
    String id,
    String sku,
    String name,
    ProductCategory category,
    BigDecimal currentPrice,
    Integer stockLevel,
    Integer reorderThreshold,
    Integer demandVelocity,
    ProductStatus status,
    BigDecimal costPrice,
    String supplierId
) {}