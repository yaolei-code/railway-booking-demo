package com.example.railway.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.railway.common.UnauthorizedException;
import com.example.railway.ticket.TicketInventory;
import com.example.railway.ticket.TicketInventoryMapper;
import com.example.railway.user.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final TicketOrderMapper orderMapper;
    private final TicketOrderItemMapper orderItemMapper;
    private final TicketInventoryMapper inventoryMapper;
    private final JwtService jwtService;
    private final long paymentTimeoutMinutes;

    public OrderService(
            TicketOrderMapper orderMapper,
            TicketOrderItemMapper orderItemMapper,
            TicketInventoryMapper inventoryMapper,
            JwtService jwtService,
            @Value("${app.order.payment-timeout-minutes:15}") long paymentTimeoutMinutes
    ) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.inventoryMapper = inventoryMapper;
        this.jwtService = jwtService;
        this.paymentTimeoutMinutes = paymentTimeoutMinutes;
    }

    @Transactional
    public OrderResponse createOrder(String authorizationHeader, CreateOrderRequest request) {
        Long userId = parseCurrentUserId(authorizationHeader);

        TicketInventory inventory = inventoryMapper.selectById(request.inventoryId());
        if (inventory == null) {
            throw new IllegalArgumentException("ticket inventory not found");
        }

        int lockedRows = inventoryMapper.lockOneTicket(inventory.getId());
        if (lockedRows == 0) {
            throw new IllegalArgumentException("ticket is sold out");
        }

        LocalDateTime now = LocalDateTime.now();

        TicketOrder order = new TicketOrder();
        order.setOrderNo(createOrderNo(userId, now));
        order.setUserId(userId);
        order.setStatus("PENDING_PAYMENT");
        order.setTotalAmount(inventory.getPrice());
        order.setCreatedAt(now);

        orderMapper.insert(order);

        TicketOrderItem item = new TicketOrderItem();
        item.setOrderId(order.getId());
        item.setPassengerName(request.passengerName());
        item.setPassengerIdNo(request.passengerIdNo());
        item.setScheduleId(inventory.getScheduleId());
        item.setDepartureStationId(inventory.getDepartureStationId());
        item.setArrivalStationId(inventory.getArrivalStationId());
        item.setSeatType(inventory.getSeatType());
        item.setPrice(inventory.getPrice());

        orderItemMapper.insert(item);

        log.info("Order created successfully: orderId={}, orderNo={}, userId={}",
                order.getId(), order.getOrderNo(), userId);
        return OrderResponse.from(order, List.of(OrderItemResponse.from(item)));
    }

    public List<OrderResponse> listMyOrders(String authorizationHeader) {
        Long userId = parseCurrentUserId(authorizationHeader);
        return orderMapper.selectList(new LambdaQueryWrapper<TicketOrder>()
                        .eq(TicketOrder::getUserId, userId)
                        .orderByDesc(TicketOrder::getCreatedAt))
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public OrderResponse getMyOrder(String authorizationHeader, Long orderId) {
        Long userId = parseCurrentUserId(authorizationHeader);
        TicketOrder order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new IllegalArgumentException("order not found");
        }
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String authorizationHeader, Long orderId) {
        Long userId = parseCurrentUserId(authorizationHeader);
        TicketOrder order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new IllegalArgumentException("order not found");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new IllegalArgumentException("only pending payment order can be cancelled");
        }

        releaseLockedInventory(order.getId());

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("Order cancelled successfully: orderId={}, orderNo={}, userId={}",
                order.getId(), order.getOrderNo(), userId);
        return toOrderResponse(order);
    }

    @Transactional
    @Scheduled(
            initialDelayString = "${app.order.timeout-initial-delay-ms:60000}",
            fixedDelayString = "${app.order.timeout-scan-ms:60000}"
    )
    public void cancelExpiredPendingOrders() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(paymentTimeoutMinutes);
        List<TicketOrder> expiredOrders = orderMapper.selectList(new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, "PENDING_PAYMENT")
                .le(TicketOrder::getCreatedAt, expiredBefore));

        for (TicketOrder order : expiredOrders) {
            releaseLockedInventory(order.getId());
            order.setStatus("CANCELLED");
            order.setCancelledAt(LocalDateTime.now());
            orderMapper.updateById(order);
            log.info("Expired pending order cancelled automatically: orderId={}, orderNo={}",
                    order.getId(), order.getOrderNo());
        }
    }

    private OrderResponse toOrderResponse(TicketOrder order) {
        List<OrderItemResponse> items = orderItemMapper.selectList(new LambdaQueryWrapper<TicketOrderItem>()
                        .eq(TicketOrderItem::getOrderId, order.getId()))
                .stream()
                .map(OrderItemResponse::from)
                .toList();
        return OrderResponse.from(order, items);
    }

    private void releaseLockedInventory(Long orderId) {
        List<TicketOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<TicketOrderItem>()
                .eq(TicketOrderItem::getOrderId, orderId));

        for (TicketOrderItem item : items) {
            int releasedRows = inventoryMapper.releaseLockedTicket(
                    item.getScheduleId(),
                    item.getDepartureStationId(),
                    item.getArrivalStationId(),
                    item.getSeatType()
            );
            if (releasedRows == 0) {
                throw new IllegalArgumentException("locked ticket count is invalid");
            }
        }
    }

    private Long parseCurrentUserId(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("missing token");
        }
        return jwtService.parseUserId(authorizationHeader.substring("Bearer ".length()));
    }

    private String createOrderNo(Long userId, LocalDateTime now) {
        return "O" + now.format(ORDER_NO_TIME_FORMATTER) + userId;
    }
}
