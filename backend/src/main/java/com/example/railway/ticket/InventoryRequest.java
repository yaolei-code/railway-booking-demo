package com.example.railway.ticket;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InventoryRequest(
        @NotNull
        Long scheduleId,

        @NotNull
        Long departureStationId,

        @NotNull
        Long arrivalStationId,

        @NotBlank
        String seatType,

        @NotNull
        @Min(0)
        Integer totalCount,

        @NotNull
        @Min(0)
        Integer availableCount,

        @NotNull
        @Min(0)
        Integer lockedCount,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price
) {
}
