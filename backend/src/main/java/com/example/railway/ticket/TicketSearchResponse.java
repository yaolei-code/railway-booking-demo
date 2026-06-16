package com.example.railway.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TicketSearchResponse(
        Long inventoryId,
        Long scheduleId,
        LocalDate travelDate,
        Long trainId,
        String trainNo,
        String trainType,
        Long departureStationId,
        String departureStationName,
        Long arrivalStationId,
        String arrivalStationName,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Long durationMinutes,
        String seatType,
        Integer availableCount,
        BigDecimal price
) {
}
