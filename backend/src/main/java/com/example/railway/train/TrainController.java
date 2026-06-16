package com.example.railway.train;

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
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/api/trains")
    public ApiResponse<List<TrainResponse>> listTrains(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(trainService.listTrains(keyword));
    }

    @PostMapping("/api/admin/trains")
    public ApiResponse<TrainResponse> createTrain(@Valid @RequestBody TrainRequest request) {
        return ApiResponse.success(trainService.createTrain(request));
    }

    @PutMapping("/api/admin/trains/{id}")
    public ApiResponse<TrainResponse> updateTrain(@PathVariable Long id, @Valid @RequestBody TrainRequest request) {
        return ApiResponse.success(trainService.updateTrain(id, request));
    }

    @DeleteMapping("/api/admin/trains/{id}")
    public ApiResponse<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ApiResponse.success();
    }

    @GetMapping("/api/trains/{id}/stations")
    public ApiResponse<List<TrainStationResponse>> listTrainStations(@PathVariable Long id) {
        return ApiResponse.success(trainService.listTrainStations(id));
    }

    @PutMapping("/api/admin/trains/{id}/stations")
    public ApiResponse<List<TrainStationResponse>> replaceTrainStations(
            @PathVariable Long id,
            @Valid @RequestBody List<TrainStationRequest> requests
    ) {
        return ApiResponse.success(trainService.replaceTrainStations(id, requests));
    }
}
