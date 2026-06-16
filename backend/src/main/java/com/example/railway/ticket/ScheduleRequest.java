package com.example.railway.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScheduleRequest(
        @NotNull
        Long trainId,

        @NotNull
        LocalDate travelDate,

        @NotBlank
        String status
) {
}
