package com.example.railway.train;

import com.example.railway.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Trains", description = "Train and stop-station APIs")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/api/trains")
    @Operation(summary = "List trains", description = "Lists trains and optionally filters by train number or type.")
    public ApiResponse<List<TrainResponse>> listTrains(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(trainService.listTrains(keyword));
    }

    @PostMapping("/api/admin/trains")
    @Operation(summary = "Create train", description = "Creates a train. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<TrainResponse> createTrain(@Valid @RequestBody TrainRequest request) {
        return ApiResponse.success(trainService.createTrain(request));
    }

    @PutMapping("/api/admin/trains/{id}")
    @Operation(summary = "Update train", description = "Updates a train. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<TrainResponse> updateTrain(@PathVariable Long id, @Valid @RequestBody TrainRequest request) {
        return ApiResponse.success(trainService.updateTrain(id, request));
    }

    @DeleteMapping("/api/admin/trains/{id}")
    @Operation(summary = "Delete train", description = "Deletes a train and its stop stations. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ApiResponse.success();
    }

    @GetMapping("/api/trains/{id}/stations")
    @Operation(summary = "List train stop stations", description = "Lists the stop stations for one train.")
    public ApiResponse<List<TrainStationResponse>> listTrainStations(@PathVariable Long id) {
        return ApiResponse.success(trainService.listTrainStations(id));
    }

    @PutMapping("/api/admin/trains/{id}/stations")
    @Operation(summary = "Replace train stop stations", description = "Replaces the full stop-station route for one train. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<List<TrainStationResponse>> replaceTrainStations(
            @PathVariable Long id,
            @Valid @RequestBody List<TrainStationRequest> requests
    ) {
        return ApiResponse.success(trainService.replaceTrainStations(id, requests));
    }
}
