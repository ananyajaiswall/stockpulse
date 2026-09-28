package com.stockpulse.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SimulateOrderDTO(
    @NotNull @Positive Integer quantity
) {}
