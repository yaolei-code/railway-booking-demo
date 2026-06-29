package com.example.railway.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Ticket inventory create request")
public record InventoryRequest(
        @Schema(description = "Daily schedule ID", example = "1")
        @NotNull
        Long scheduleId,

        @Schema(description = "Departure station ID", example = "1")
        @NotNull
        Long departureStationId,

        @Schema(description = "Arrival station ID", example = "5")
        @NotNull
        Long arrivalStationId,

        @Schema(description = "Seat type", example = "SECOND_CLASS")
        @NotBlank
        String seatType,

        @Schema(description = "Total inventory count", example = "100")
        @NotNull
        @Min(0)
        Integer totalCount,

        @Schema(description = "Available inventory count", example = "100")
        @NotNull
        @Min(0)
        Integer availableCount,

        @Schema(description = "Locked inventory count", example = "0")
        @NotNull
        @Min(0)
        Integer lockedCount,

        @Schema(description = "Ticket price", example = "553.00")
        @NotNull
        @DecimalMin("0.00")
        BigDecimal price
) {
}
