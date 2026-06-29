package com.example.railway.station;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Station create or update request")
public record StationRequest(
        @Schema(description = "Station name", example = "北京南")
        @NotBlank
        @Size(max = 50)
        String name,

        @Schema(description = "City name", example = "北京")
        @NotBlank
        @Size(max = 50)
        String city,

        @Schema(description = "Station code", example = "BJN")
        @NotBlank
        @Size(max = 20)
        String code
) {
}
