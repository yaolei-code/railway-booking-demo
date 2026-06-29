package com.example.railway.train;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Train create or update request")
public record TrainRequest(
        @Schema(description = "Train number", example = "G101")
        @NotBlank
        @Size(max = 20)
        String trainNo,

        @Schema(description = "Train type", example = "G")
        @NotBlank
        @Size(max = 20)
        String trainType,

        @Schema(description = "Train status", example = "ACTIVE")
        @NotBlank
        @Size(max = 20)
        String status
) {
}
