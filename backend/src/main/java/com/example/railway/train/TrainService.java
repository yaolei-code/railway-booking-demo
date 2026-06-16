package com.example.railway.train;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.railway.station.Station;
import com.example.railway.station.StationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainService.class);

    private final TrainMapper trainMapper;
    private final TrainStationMapper trainStationMapper;
    private final StationMapper stationMapper;

    public TrainService(TrainMapper trainMapper, TrainStationMapper trainStationMapper, StationMapper stationMapper) {
        this.trainMapper = trainMapper;
        this.trainStationMapper = trainStationMapper;
        this.stationMapper = stationMapper;
    }

    public List<TrainResponse> listTrains(String keyword) {
        LambdaQueryWrapper<Train> queryWrapper = new LambdaQueryWrapper<Train>()
                .orderByAsc(Train::getTrainNo);

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Train::getTrainNo, keyword)
                    .or()
                    .like(Train::getTrainType, keyword));
        }

        return trainMapper.selectList(queryWrapper).stream()
                .map(TrainResponse::from)
                .toList();
    }

    public TrainResponse createTrain(TrainRequest request) {
        ensureTrainNoAvailable(request.trainNo(), null);

        LocalDateTime now = LocalDateTime.now();

        Train train = new Train();
        train.setTrainNo(request.trainNo());
        train.setTrainType(request.trainType());
        train.setStatus(request.status());
        train.setCreatedAt(now);
        train.setUpdatedAt(now);

        trainMapper.insert(train);
        log.info("Train created successfully: trainId={}, trainNo={}", train.getId(), train.getTrainNo());
        return TrainResponse.from(train);
    }

    public TrainResponse updateTrain(Long id, TrainRequest request) {
        Train train = findTrainOrThrow(id);
        ensureTrainNoAvailable(request.trainNo(), id);

        train.setTrainNo(request.trainNo());
        train.setTrainType(request.trainType());
        train.setStatus(request.status());
        train.setUpdatedAt(LocalDateTime.now());

        trainMapper.updateById(train);
        log.info("Train updated successfully: trainId={}, trainNo={}", train.getId(), train.getTrainNo());
        return TrainResponse.from(train);
    }

    @Transactional
    public void deleteTrain(Long id) {
        Train train = findTrainOrThrow(id);
        trainStationMapper.delete(new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainId, id));
        trainMapper.deleteById(id);
        log.info("Train deleted successfully: trainId={}, trainNo={}", train.getId(), train.getTrainNo());
    }

    public List<TrainStationResponse> listTrainStations(Long trainId) {
        findTrainOrThrow(trainId);
        return trainStationMapper.selectList(new LambdaQueryWrapper<TrainStation>()
                        .eq(TrainStation::getTrainId, trainId)
                        .orderByAsc(TrainStation::getStopOrder))
                .stream()
                .map(this::toTrainStationResponse)
                .toList();
    }

    @Transactional
    public List<TrainStationResponse> replaceTrainStations(Long trainId, List<TrainStationRequest> requests) {
        findTrainOrThrow(trainId);
        validateTrainStationRequests(requests);

        trainStationMapper.delete(new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainId, trainId));

        for (TrainStationRequest request : requests) {
            TrainStation trainStation = new TrainStation();
            trainStation.setTrainId(trainId);
            trainStation.setStationId(request.stationId());
            trainStation.setStopOrder(request.stopOrder());
            trainStation.setArrivalTime(request.arrivalTime());
            trainStation.setDepartureTime(request.departureTime());
            trainStationMapper.insert(trainStation);
        }

        log.info("Train stations replaced successfully: trainId={}, stopCount={}", trainId, requests.size());
        return listTrainStations(trainId);
    }

    private Train findTrainOrThrow(Long id) {
        Train train = trainMapper.selectById(id);
        if (train == null) {
            throw new IllegalArgumentException("train not found");
        }
        return train;
    }

    private void ensureTrainNoAvailable(String trainNo, Long currentTrainId) {
        Long sameTrainNoCount = trainMapper.selectCount(new LambdaQueryWrapper<Train>()
                .eq(Train::getTrainNo, trainNo)
                .ne(currentTrainId != null, Train::getId, currentTrainId));
        if (sameTrainNoCount > 0) {
            log.warn("Train operation rejected because train number already exists: trainNo={}", trainNo);
            throw new IllegalArgumentException("train number already exists");
        }
    }

    private void validateTrainStationRequests(List<TrainStationRequest> requests) {
        if (requests == null || requests.size() < 2) {
            throw new IllegalArgumentException("train must have at least two stations");
        }

        Set<Integer> stopOrders = new HashSet<>();
        Set<Long> stationIds = new HashSet<>();

        for (TrainStationRequest request : requests) {
            if (!stopOrders.add(request.stopOrder())) {
                throw new IllegalArgumentException("stop order must be unique");
            }
            if (!stationIds.add(request.stationId())) {
                throw new IllegalArgumentException("station must be unique in one train route");
            }
            Station station = stationMapper.selectById(request.stationId());
            if (station == null) {
                throw new IllegalArgumentException("station not found: " + request.stationId());
            }
        }
    }

    private TrainStationResponse toTrainStationResponse(TrainStation trainStation) {
        Station station = stationMapper.selectById(trainStation.getStationId());
        if (station == null) {
            throw new IllegalArgumentException("station not found: " + trainStation.getStationId());
        }
        return TrainStationResponse.from(trainStation, station);
    }
}
