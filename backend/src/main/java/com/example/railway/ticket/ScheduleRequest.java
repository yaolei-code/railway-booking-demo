package com.example.railway.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Daily train schedule request")
public record ScheduleRequest(
        @Schema(description = "Train ID", example = "1")
        @NotNull
        Long trainId,

        @Schema(description = "Travel date", example = "2026-06-20")
        @NotNull
        LocalDate travelDate,

        @Schema(description = "Schedule status", example = "OPEN")
        @NotBlank
        String status
) {
}
