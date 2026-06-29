package com.example.railway.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login request")
public record LoginRequest(
        @Schema(description = "Username", example = "testuser")
        @NotBlank
        String username,

        @Schema(description = "Password", example = "123456")
        @NotBlank
        String password
) {
}
