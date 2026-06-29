package com.example.railway.health;

import com.example.railway.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Service health check")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "Health check", description = "Checks whether the backend service is running.")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "service", "railway-booking-backend",
                "time", OffsetDateTime.now().toString()
        ));
    }
}
