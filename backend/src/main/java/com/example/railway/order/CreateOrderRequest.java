package com.example.railway.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull
        Long inventoryId,

        @NotBlank
        String passengerName,

        @NotBlank
        String passengerIdNo
) {
}
