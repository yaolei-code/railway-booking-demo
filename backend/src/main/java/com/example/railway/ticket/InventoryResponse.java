package com.example.railway.ticket;

import java.math.BigDecimal;

public record InventoryResponse(
        Long id,
        Long scheduleId,
        Long departureStationId,
        Long arrivalStationId,
        String seatType,
        Integer totalCount,
        Integer availableCount,
        Integer lockedCount,
        BigDecimal price
) {
    public static InventoryResponse from(TicketInventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getScheduleId(),
                inventory.getDepartureStationId(),
                inventory.getArrivalStationId(),
                inventory.getSeatType(),
                inventory.getTotalCount(),
                inventory.getAvailableCount(),
                inventory.getLockedCount(),
                inventory.getPrice()
        );
    }
}
