package com.example.railway.ticket;

import com.example.railway.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/api/tickets/search")
    public ApiResponse<List<TicketSearchResponse>> searchTickets(
            @RequestParam @NotNull Long departureStationId,
            @RequestParam @NotNull Long arrivalStationId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate,
            @RequestParam(required = false) String seatType
    ) {
        return ApiResponse.success(ticketService.searchTickets(departureStationId, arrivalStationId, travelDate, seatType));
    }

    @PostMapping("/api/admin/schedules")
    public ApiResponse<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        return ApiResponse.success(ticketService.createSchedule(request));
    }

    @PostMapping("/api/admin/inventory")
    public ApiResponse<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        return ApiResponse.success(ticketService.createInventory(request));
    }
}
