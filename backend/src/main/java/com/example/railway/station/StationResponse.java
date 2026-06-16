package com.example.railway.station;

import java.time.LocalDateTime;

public record StationResponse(
        Long id,
        String name,
        String city,
        String code,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCity(),
                station.getCode(),
                station.getCreatedAt(),
                station.getUpdatedAt()
        );
    }
}
