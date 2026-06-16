package com.example.railway.station;

import com.example.railway.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/api/stations")
    public ApiResponse<List<StationResponse>> listStations(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(stationService.listStations(keyword));
    }

    @PostMapping("/api/admin/stations")
    public ApiResponse<StationResponse> createStation(@Valid @RequestBody StationRequest request) {
        return ApiResponse.success(stationService.createStation(request));
    }

    @PutMapping("/api/admin/stations/{id}")
    public ApiResponse<StationResponse> updateStation(@PathVariable Long id, @Valid @RequestBody StationRequest request) {
        return ApiResponse.success(stationService.updateStation(id, request));
    }

    @DeleteMapping("/api/admin/stations/{id}")
    public ApiResponse<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ApiResponse.success();
    }
}
