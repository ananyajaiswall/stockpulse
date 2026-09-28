package com.stockpulse.product.mapper;

import com.stockpulse.product.Product;
import com.stockpulse.product.ProductStatus;
import com.stockpulse.product.dto.ProductDTO;
import com.stockpulse.product.dto.CreateProductDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {
    
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductDTO(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getCategory(),
            product.getCurrentPrice(),
            product.getStockLevel(),
            product.getReorderThreshold(),
            product.getDemandVelocity(),
            product.getStatus(),
            product.getCostPrice(),
            product.getSupplierId()
        );
    }
    
    public Product toEntity(CreateProductDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product(
            dto.id(),
            dto.sku(),
            dto.name(),
            dto.category(),
            dto.currentPrice() != null ? dto.currentPrice() : BigDecimal.ZERO,
            dto.stockLevel() != null ? dto.stockLevel() : 0,
            dto.reorderThreshold() != null ? dto.reorderThreshold() : 0,
            dto.demandVelocity() != null ? dto.demandVelocity() : 0,
            dto.status() != null ? dto.status() : ProductStatus.ACTIVE
        );
        product.setCostPrice(dto.costPrice());
        product.setSupplierId(dto.supplierId());
        return product;
    }
}