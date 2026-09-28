package com.stockpulse.pricing.dto;

import com.stockpulse.pricing.PriceDirection;
import com.stockpulse.pricing.SuggestionStatus;
import com.stockpulse.common.TriggerReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PricingSuggestionDTO(
    String id,
    String productId,
    String productName,
    BigDecimal currentPrice,
    BigDecimal recommendedPrice,
    PriceDirection direction,
    Double confidence,
    String reasoning,
    SuggestionStatus status,
    TriggerReason triggerReason,
    LocalDateTime createdAt
) {}