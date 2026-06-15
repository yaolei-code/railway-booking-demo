package com.example.railway.health;

import com.example.railway.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "service", "railway-booking-backend",
                "time", OffsetDateTime.now().toString()
        ));
    }
}
