package com.example.railway.order;

import com.example.railway.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ApiResponse.success(orderService.createOrder(authorizationHeader, request));
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> listMyOrders(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return ApiResponse.success(orderService.listMyOrders(authorizationHeader));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getMyOrder(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        return ApiResponse.success(orderService.getMyOrder(authorizationHeader, id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        return ApiResponse.success(orderService.cancelOrder(authorizationHeader, id));
    }
}
