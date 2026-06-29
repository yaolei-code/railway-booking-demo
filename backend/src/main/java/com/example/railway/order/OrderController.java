package com.example.railway.order;

import com.example.railway.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Order creation, query, and cancellation APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a pending-payment ticket order and locks one inventory item.")
    public ApiResponse<OrderResponse> createOrder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ApiResponse.success(orderService.createOrder(authorizationHeader, request));
    }

    @GetMapping
    @Operation(summary = "List my orders", description = "Lists orders that belong to the current user.")
    public ApiResponse<List<OrderResponse>> listMyOrders(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return ApiResponse.success(orderService.listMyOrders(authorizationHeader));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get my order detail", description = "Returns one order if it belongs to the current user.")
    public ApiResponse<OrderResponse> getMyOrder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        return ApiResponse.success(orderService.getMyOrder(authorizationHeader, id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels a pending-payment order and releases locked inventory.")
    public ApiResponse<OrderResponse> cancelOrder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        return ApiResponse.success(orderService.cancelOrder(authorizationHeader, id));
    }
}
