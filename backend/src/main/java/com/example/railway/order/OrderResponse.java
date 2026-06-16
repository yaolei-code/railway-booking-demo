package com.example.railway.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNo,
        Long userId,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime cancelledAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(TicketOrder order, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getPaidAt(),
                order.getCancelledAt(),
                items
        );
    }
}
