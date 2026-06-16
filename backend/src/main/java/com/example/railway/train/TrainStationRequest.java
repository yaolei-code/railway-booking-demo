package com.example.railway.train;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record TrainStationRequest(
        @NotNull
        Long stationId,

        @NotNull
        @Positive
        Integer stopOrder,

        LocalTime arrivalTime,

        LocalTime departureTime
) {
}
