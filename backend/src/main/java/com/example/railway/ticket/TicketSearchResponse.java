package com.example.railway.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TicketSearchResponse {

    private Long inventoryId;
    private Long scheduleId;
    private LocalDate travelDate;
    private Long trainId;
    private String trainNo;
    private String trainType;
    private Long departureStationId;
    private String departureStationName;
    private Long arrivalStationId;
    private String arrivalStationName;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Long durationMinutes;
    private String seatType;
    private Integer availableCount;
    private BigDecimal price;

    public TicketSearchResponse() {
    }

    public TicketSearchResponse(
            Long inventoryId, Long scheduleId, LocalDate travelDate,
            Long trainId, String trainNo, String trainType,
            Long departureStationId, String departureStationName,
            Long arrivalStationId, String arrivalStationName,
            LocalTime departureTime, LocalTime arrivalTime,
            Long durationMinutes, String seatType,
            Integer availableCount, BigDecimal price
    ) {
        this.inventoryId = inventoryId;
        this.scheduleId = scheduleId;
        this.travelDate = travelDate;
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.trainType = trainType;
        this.departureStationId = departureStationId;
        this.departureStationName = departureStationName;
        this.arrivalStationId = arrivalStationId;
        this.arrivalStationName = arrivalStationName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationMinutes = durationMinutes;
        this.seatType = seatType;
        this.availableCount = availableCount;
        this.price = price;
    }

    // ---- getters ----

    public Long getInventoryId() { return inventoryId; }
    public Long getScheduleId() { return scheduleId; }
    public LocalDate getTravelDate() { return travelDate; }
    public Long getTrainId() { return trainId; }
    public String getTrainNo() { return trainNo; }
    public String getTrainType() { return trainType; }
    public Long getDepartureStationId() { return departureStationId; }
    public String getDepartureStationName() { return departureStationName; }
    public Long getArrivalStationId() { return arrivalStationId; }
    public String getArrivalStationName() { return arrivalStationName; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public Long getDurationMinutes() { return durationMinutes; }
    public String getSeatType() { return seatType; }
    public Integer getAvailableCount() { return availableCount; }
    public BigDecimal getPrice() { return price; }

    // ---- setters (only the ones that need mutation) ----

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }
}
