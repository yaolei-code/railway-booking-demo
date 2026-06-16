package com.example.railway.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String passengerName,
        String passengerIdNo,
        Long scheduleId,
        Long departureStationId,
        Long arrivalStationId,
        String seatType,
        BigDecimal price
) {
    public static OrderItemResponse from(TicketOrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getPassengerName(),
                item.getPassengerIdNo(),
                item.getScheduleId(),
                item.getDepartureStationId(),
                item.getArrivalStationId(),
                item.getSeatType(),
                item.getPrice()
        );
    }
}
