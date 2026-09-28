package com.stockpulse.product;

import com.stockpulse.product.dto.CreateProductDTO;
import com.stockpulse.product.dto.UpdateStockDTO;
import com.stockpulse.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    public Product createProduct(CreateProductDTO createProductDTO) {
        Product product = productMapper.toEntity(createProductDTO);
        return productRepository.save(product);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    public Product updateStock(String productId, UpdateStockDTO updateStockDTO) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        product.setStockLevel(updateStockDTO.stockLevel());
        return productRepository.save(product);
    }
    
    public Product simulateOrder(String productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        if (product.getStockLevel() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getSku());
        }
        
        product.decreaseStock(quantity);
        product.increaseDemandVelocity(1);
        return productRepository.save(product);
    }
    
    public Double getCategoryAverageDemandVelocity(ProductCategory category) {
        Double average = productRepository.findAverageDemandVelocityByCategory(category);
        return average != null ? average : 0.0;
    }
}