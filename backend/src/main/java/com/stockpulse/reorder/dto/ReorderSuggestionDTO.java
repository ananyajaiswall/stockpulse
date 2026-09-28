package com.stockpulse.reorder.dto;

import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.common.TriggerReason;

import java.time.LocalDateTime;

public record ReorderSuggestionDTO(
    String id,
    String productId,
    String productName,
    Integer currentStock,
    Integer recommendedQuantity,
    Integer suggestedLeadTimeDays,
    Double confidence,
    String reasoning,
    SuggestionStatus status,
    TriggerReason triggerReason,
    LocalDateTime createdAt
) {}