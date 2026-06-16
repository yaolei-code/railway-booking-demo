package com.example.railway.train;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrainRequest(
        @NotBlank
        @Size(max = 20)
        String trainNo,

        @NotBlank
        @Size(max = 20)
        String trainType,

        @NotBlank
        @Size(max = 20)
        String status
) {
}
