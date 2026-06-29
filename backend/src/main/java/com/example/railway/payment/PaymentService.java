package com.example.railway.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.railway.common.UnauthorizedException;
import com.example.railway.order.TicketOrder;
import com.example.railway.order.TicketOrderItem;
import com.example.railway.order.TicketOrderItemMapper;
import com.example.railway.order.TicketOrderMapper;
import com.example.railway.ticket.RedisInventoryService;
import com.example.railway.ticket.TicketInventoryMapper;
import com.example.railway.user.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final DateTimeFormatter PAYMENT_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final PaymentMapper paymentMapper;
    private final TicketOrderMapper orderMapper;
    private final TicketOrderItemMapper orderItemMapper;
    private final TicketInventoryMapper inventoryMapper;
    private final RedisInventoryService redisInventoryService;
    private final JwtService jwtService;

    public PaymentService(
            PaymentMapper paymentMapper,
            TicketOrderMapper orderMapper,
            TicketOrderItemMapper orderItemMapper,
            TicketInventoryMapper inventoryMapper,
            RedisInventoryService redisInventoryService,
            JwtService jwtService
    ) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.inventoryMapper = inventoryMapper;
        this.redisInventoryService = redisInventoryService;
        this.jwtService = jwtService;
    }

    @Transactional
    public PaymentResponse payOrder(String authorizationHeader, Long orderId) {
        Long userId = parseCurrentUserId(authorizationHeader);

        TicketOrder order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new IllegalArgumentException("order not found");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new IllegalArgumentException("only pending payment order can be paid");
        }

        Long existingPaymentCount = paymentMapper.selectCount(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, orderId)
                .eq(Payment::getStatus, "SUCCESS"));
        if (existingPaymentCount > 0) {
            throw new IllegalArgumentException("order has already been paid");
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentNo(createPaymentNo(userId, now));
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("SUCCESS");
        payment.setPaidAt(now);
        paymentMapper.insert(payment);

        order.setStatus("PAID");
        order.setPaidAt(now);
        orderMapper.updateById(order);

        releaseLockedTicketCount(order.getId());

        log.info("Order paid successfully: orderId={}, paymentId={}, userId={}",
                order.getId(), payment.getId(), userId);
        return PaymentResponse.from(payment);
    }

    private void releaseLockedTicketCount(Long orderId) {
        List<TicketOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<TicketOrderItem>()
                .eq(TicketOrderItem::getOrderId, orderId));

        for (TicketOrderItem item : items) {
            int confirmedRows = inventoryMapper.confirmLockedTicket(
                    item.getScheduleId(),
                    item.getDepartureStationId(),
                    item.getArrivalStationId(),
                    item.getSeatType()
            );
            if (confirmedRows == 0) {
                throw new IllegalArgumentException("locked ticket count is invalid");
            }
            // 同步 Redis：支付成功后 lockedCount - 1
            if (item.getInventoryId() != null) {
                redisInventoryService.confirmOne(item.getInventoryId());
            }
        }
    }

    private Long parseCurrentUserId(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("missing token");
        }
        return jwtService.parseUserId(authorizationHeader.substring("Bearer ".length()));
    }

    private String createPaymentNo(Long userId, LocalDateTime now) {
        return "P" + now.format(PAYMENT_NO_TIME_FORMATTER) + userId;
    }
}
