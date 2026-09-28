package com.stockpulse.common;

import com.stockpulse.product.ProductNotFoundException;
import com.stockpulse.product.InsufficientStockException;
import com.stockpulse.pricing.PricingSuggestionNotFoundException;
import com.stockpulse.reorder.ReorderSuggestionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("PRODUCT_NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("INSUFFICIENT_STOCK", ex.getMessage()));
    }
    
    @ExceptionHandler(PricingSuggestionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePricingSuggestionNotFound(PricingSuggestionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("PRICING_SUGGESTION_NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(ReorderSuggestionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReorderSuggestionNotFound(ReorderSuggestionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("REORDER_SUGGESTION_NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("ILLEGAL_STATE", ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("ILLEGAL_ARGUMENT", ex.getMessage()));
    }
    
    public record ErrorResponse(String code, String message) {}
}