package com.example.railway.station;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StationRequest(
        @NotBlank
        @Size(max = 50)
        String name,

        @NotBlank
        @Size(max = 50)
        String city,

        @NotBlank
        @Size(max = 20)
        String code
) {
}
