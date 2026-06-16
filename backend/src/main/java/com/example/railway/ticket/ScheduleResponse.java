package com.example.railway.ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        Long trainId,
        LocalDate travelDate,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScheduleResponse from(TrainDailySchedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTrainId(),
                schedule.getTravelDate(),
                schedule.getStatus(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
