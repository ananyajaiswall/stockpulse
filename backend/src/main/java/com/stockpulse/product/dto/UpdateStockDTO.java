package com.stockpulse.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateStockDTO(
    @NotNull @PositiveOrZero Integer stockLevel
) {}
