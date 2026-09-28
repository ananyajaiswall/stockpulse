package com.stockpulse.pricing;

public class PricingSuggestionNotFoundException extends RuntimeException {
    public PricingSuggestionNotFoundException(String message) {
        super(message);
    }
}