package com.stockpulse.product.controller;

import com.stockpulse.product.Product;
import com.stockpulse.product.ProductCategory;
import com.stockpulse.product.ProductService;
import com.stockpulse.product.ProductStatus;
import com.stockpulse.product.dto.CreateProductDTO;
import com.stockpulse.product.dto.ProductDTO;
import com.stockpulse.product.dto.UpdateStockDTO;
import com.stockpulse.product.dto.SimulateOrderDTO;
import com.stockpulse.product.mapper.ProductMapper;
import com.stockpulse.commerce.CommerceService;
import com.stockpulse.common.TriggerReason;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    private final CommerceService commerceService;
    private final ProductMapper productMapper;
    
    public ProductController(ProductService productService, CommerceService commerceService, ProductMapper productMapper) {
        this.productService = productService;
        this.commerceService = commerceService;
        this.productMapper = productMapper;
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        Product product = productService.createProduct(createProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDTO(product));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) ProductCategory category) {
        
        List<Product> products;
        if (status != null && category != null) {
            products = productService.getProductsByStatus(status).stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
        } else if (status != null) {
            products = productService.getProductsByStatus(status);
        } else if (category != null) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        
        List<ProductDTO> productDTOs = products.stream()
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable String id, @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        Product updatedProduct = productService.updateStock(id, updateStockDTO);
        
        // Check if this triggers a low stock condition
        if (updatedProduct.isLowStock()) {
            commerceService.triggerCommerceReview(id, TriggerReason.INVENTORY_LOW);
        }
        
        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }
    
    @PostMapping("/{id}/orders")
    public ResponseEntity<ProductDTO> simulateOrder(@PathVariable String id, @Valid @RequestBody SimulateOrderDTO orderDTO) {
        Product updatedProduct = productService.simulateOrder(id, orderDTO.quantity());
        
        // Check for triggers
        TriggerReason triggerReason = determineTriggerReason(updatedProduct);
        if (triggerReason != null) {
            commerceService.triggerCommerceReview(id, triggerReason);
        }
        
        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }
    
    @PostMapping("/{id}/suggest-pricing")
    public ResponseEntity<?> createPricingSuggestion(@PathVariable String id) {
        try {
            var suggestion = commerceService.createPricingSuggestion(id, TriggerReason.MANUAL);
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/suggest-reorder")
    public ResponseEntity<?> createReorderSuggestion(@PathVariable String id) {
        try {
            var suggestion = commerceService.createReorderSuggestion(id, TriggerReason.MANUAL);
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private TriggerReason determineTriggerReason(Product product) {
        if (product.isLowStock()) {
            return TriggerReason.INVENTORY_LOW;
        }
        
        // Check for demand spike (3x category average by default)
        Double categoryAverage = productService.getCategoryAverageDemandVelocity(product.getCategory());
        if (categoryAverage > 0 && product.getDemandVelocity() > categoryAverage * 3) {
            return TriggerReason.DEMAND_SPIKE;
        }
        
        return null;
    }
}