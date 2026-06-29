package com.example.railway.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record RegisterRequest(
        @Schema(description = "Username", example = "testuser")
        @NotBlank
        @Size(min = 3, max = 30)
        String username,

        @Schema(description = "Password", example = "123456")
        @NotBlank
        @Size(min = 6, max = 50)
        String password,

        @Schema(description = "Phone number", example = "13800000000")
        String phone,

        @Schema(description = "Email address", example = "test@example.com")
        String email
) {
}
