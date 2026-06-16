package com.example.railway.train;

import com.example.railway.station.Station;

import java.time.LocalTime;

public record TrainStationResponse(
        Long id,
        Long trainId,
        Long stationId,
        String stationName,
        String city,
        Integer stopOrder,
        LocalTime arrivalTime,
        LocalTime departureTime
) {
    public static TrainStationResponse from(TrainStation trainStation, Station station) {
        return new TrainStationResponse(
                trainStation.getId(),
                trainStation.getTrainId(),
                trainStation.getStationId(),
                station.getName(),
                station.getCity(),
                trainStation.getStopOrder(),
                trainStation.getArrivalTime(),
                trainStation.getDepartureTime()
        );
    }
}
