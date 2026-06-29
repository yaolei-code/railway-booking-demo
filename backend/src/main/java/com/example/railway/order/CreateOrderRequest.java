package com.example.railway.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Create ticket order request")
public record CreateOrderRequest(
        @Schema(description = "Inventory ID returned by ticket search", example = "1")
        @NotNull
        Long inventoryId,

        @Schema(description = "Passenger name", example = "张三")
        @NotBlank
        String passengerName,

        @Schema(description = "Passenger ID number", example = "110101199001011234")
        @NotBlank
        String passengerIdNo
) {
}
