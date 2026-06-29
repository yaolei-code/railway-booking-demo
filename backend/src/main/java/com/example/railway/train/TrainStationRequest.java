package com.example.railway.train;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

@Schema(description = "Train stop-station request")
public record TrainStationRequest(
        @Schema(description = "Station ID", example = "1")
        @NotNull
        Long stationId,

        @Schema(description = "Stop order in the route", example = "1")
        @NotNull
        @Positive
        Integer stopOrder,

        @Schema(description = "Arrival time", example = "09:30:00", nullable = true)
        LocalTime arrivalTime,

        @Schema(description = "Departure time", example = "09:35:00", nullable = true)
        LocalTime departureTime
) {
}
