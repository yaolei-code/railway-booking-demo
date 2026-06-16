package com.example.railway.station;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StationService {

    private static final Logger log = LoggerFactory.getLogger(StationService.class);

    private final StationMapper stationMapper;

    public StationService(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    public List<StationResponse> listStations(String keyword) {
        LambdaQueryWrapper<Station> queryWrapper = new LambdaQueryWrapper<Station>()
                .orderByAsc(Station::getCity)
                .orderByAsc(Station::getName);

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Station::getName, keyword)
                    .or()
                    .like(Station::getCity, keyword)
                    .or()
                    .like(Station::getCode, keyword));
        }

        return stationMapper.selectList(queryWrapper).stream()
                .map(StationResponse::from)
                .toList();
    }

    public StationResponse createStation(StationRequest request) {
        ensureNameAndCodeAvailable(request.name(), request.code(), null);

        LocalDateTime now = LocalDateTime.now();

        Station station = new Station();
        station.setName(request.name());
        station.setCity(request.city());
        station.setCode(request.code());
        station.setCreatedAt(now);
        station.setUpdatedAt(now);

        stationMapper.insert(station);
        log.info("Station created successfully: stationId={}, name={}, code={}",
                station.getId(), station.getName(), station.getCode());
        return StationResponse.from(station);
    }

    public StationResponse updateStation(Long id, StationRequest request) {
        Station station = stationMapper.selectById(id);
        if (station == null) {
            throw new IllegalArgumentException("station not found");
        }

        ensureNameAndCodeAvailable(request.name(), request.code(), id);

        station.setName(request.name());
        station.setCity(request.city());
        station.setCode(request.code());
        station.setUpdatedAt(LocalDateTime.now());

        stationMapper.updateById(station);
        log.info("Station updated successfully: stationId={}, name={}, code={}",
                station.getId(), station.getName(), station.getCode());
        return StationResponse.from(station);
    }

    public void deleteStation(Long id) {
        Station station = stationMapper.selectById(id);
        if (station == null) {
            throw new IllegalArgumentException("station not found");
        }

        stationMapper.deleteById(id);
        log.info("Station deleted successfully: stationId={}, name={}, code={}",
                station.getId(), station.getName(), station.getCode());
    }

    private void ensureNameAndCodeAvailable(String name, String code, Long currentStationId) {
        Long sameNameCount = stationMapper.selectCount(new LambdaQueryWrapper<Station>()
                .eq(Station::getName, name)
                .ne(currentStationId != null, Station::getId, currentStationId));
        if (sameNameCount > 0) {
            log.warn("Station operation rejected because name already exists: name={}", name);
            throw new IllegalArgumentException("station name already exists");
        }

        Long sameCodeCount = stationMapper.selectCount(new LambdaQueryWrapper<Station>()
                .eq(Station::getCode, code)
                .ne(currentStationId != null, Station::getId, currentStationId));
        if (sameCodeCount > 0) {
            log.warn("Station operation rejected because code already exists: code={}", code);
            throw new IllegalArgumentException("station code already exists");
        }
    }
}
