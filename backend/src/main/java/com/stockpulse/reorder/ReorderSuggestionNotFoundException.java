package com.stockpulse.reorder;

public class ReorderSuggestionNotFoundException extends RuntimeException {
    public ReorderSuggestionNotFoundException(String message) {
        super(message);
    }
}