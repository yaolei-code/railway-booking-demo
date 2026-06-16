package com.example.railway.train;

import java.time.LocalDateTime;

public record TrainResponse(
        Long id,
        String trainNo,
        String trainType,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TrainResponse from(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getTrainNo(),
                train.getTrainType(),
                train.getStatus(),
                train.getCreatedAt(),
                train.getUpdatedAt()
        );
    }
}
