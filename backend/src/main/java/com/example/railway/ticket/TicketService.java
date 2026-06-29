package com.example.railway.ticket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.railway.station.Station;
import com.example.railway.station.StationMapper;
import com.example.railway.train.Train;
import com.example.railway.train.TrainMapper;
import com.example.railway.train.TrainStation;
import com.example.railway.train.TrainStationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TrainDailyScheduleMapper scheduleMapper;
    private final TicketInventoryMapper inventoryMapper;
    private final RedisInventoryService redisInventoryService;
    private final TrainMapper trainMapper;
    private final TrainStationMapper trainStationMapper;
    private final StationMapper stationMapper;

    public TicketService(
            TrainDailyScheduleMapper scheduleMapper,
            TicketInventoryMapper inventoryMapper,
            RedisInventoryService redisInventoryService,
            TrainMapper trainMapper,
            TrainStationMapper trainStationMapper,
            StationMapper stationMapper
    ) {
        this.scheduleMapper = scheduleMapper;
        this.inventoryMapper = inventoryMapper;
        this.redisInventoryService = redisInventoryService;
        this.trainMapper = trainMapper;
        this.trainStationMapper = trainStationMapper;
        this.stationMapper = stationMapper;
    }

    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Train train = trainMapper.selectById(request.trainId());
        if (train == null) {
            throw new IllegalArgumentException("train not found");
        }

        Long sameScheduleCount = scheduleMapper.selectCount(new LambdaQueryWrapper<TrainDailySchedule>()
                .eq(TrainDailySchedule::getTrainId, request.trainId())
                .eq(TrainDailySchedule::getTravelDate, request.travelDate()));
        if (sameScheduleCount > 0) {
            throw new IllegalArgumentException("schedule already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        TrainDailySchedule schedule = new TrainDailySchedule();
        schedule.setTrainId(request.trainId());
        schedule.setTravelDate(request.travelDate());
        schedule.setStatus(request.status());
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);

        scheduleMapper.insert(schedule);
        log.info("Schedule created successfully: scheduleId={}, trainId={}, travelDate={}",
                schedule.getId(), schedule.getTrainId(), schedule.getTravelDate());
        return ScheduleResponse.from(schedule);
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        TrainDailySchedule schedule = scheduleMapper.selectById(request.scheduleId());
        if (schedule == null) {
            throw new IllegalArgumentException("schedule not found");
        }
        if (request.availableCount() + request.lockedCount() > request.totalCount()) {
            throw new IllegalArgumentException("available count plus locked count cannot exceed total count");
        }

        TrainStation departureStop = findTrainStation(schedule.getTrainId(), request.departureStationId());
        TrainStation arrivalStop = findTrainStation(schedule.getTrainId(), request.arrivalStationId());
        if (departureStop == null || arrivalStop == null) {
            throw new IllegalArgumentException("departure or arrival station is not in this train route");
        }
        if (departureStop.getStopOrder() >= arrivalStop.getStopOrder()) {
            throw new IllegalArgumentException("departure station must be before arrival station");
        }

        Long sameInventoryCount = inventoryMapper.selectCount(new LambdaQueryWrapper<TicketInventory>()
                .eq(TicketInventory::getScheduleId, request.scheduleId())
                .eq(TicketInventory::getDepartureStationId, request.departureStationId())
                .eq(TicketInventory::getArrivalStationId, request.arrivalStationId())
                .eq(TicketInventory::getSeatType, request.seatType()));
        if (sameInventoryCount > 0) {
            throw new IllegalArgumentException("ticket inventory already exists");
        }

        TicketInventory inventory = new TicketInventory();
        inventory.setScheduleId(request.scheduleId());
        inventory.setDepartureStationId(request.departureStationId());
        inventory.setArrivalStationId(request.arrivalStationId());
        inventory.setSeatType(request.seatType());
        inventory.setTotalCount(request.totalCount());
        inventory.setAvailableCount(request.availableCount());
        inventory.setLockedCount(request.lockedCount());
        inventory.setPrice(request.price());

        inventoryMapper.insert(inventory);
        // 同步到 Redis，让后续搜索和下单可以走缓存
        redisInventoryService.saveInventory(inventory);
        log.info("Ticket inventory created successfully: inventoryId={}, scheduleId={}, seatType={}",
                inventory.getId(), inventory.getScheduleId(), inventory.getSeatType());
        return InventoryResponse.from(inventory);
    }

    public List<TicketSearchResponse> searchTickets(
            Long departureStationId,
            Long arrivalStationId,
            LocalDate travelDate,
            String seatType
    ) {
        if (Objects.equals(departureStationId, arrivalStationId)) {
            throw new IllegalArgumentException("departure station and arrival station cannot be the same");
        }

        LambdaQueryWrapper<TicketInventory> queryWrapper = new LambdaQueryWrapper<TicketInventory>()
                .eq(TicketInventory::getDepartureStationId, departureStationId)
                .eq(TicketInventory::getArrivalStationId, arrivalStationId)
                .gt(TicketInventory::getAvailableCount, 0);

        if (StringUtils.hasText(seatType)) {
            queryWrapper.eq(TicketInventory::getSeatType, seatType);
        }

        List<TicketInventory> inventories = inventoryMapper.selectList(queryWrapper);
        List<TicketSearchResponse> result = new ArrayList<>();

        for (TicketInventory inventory : inventories) {
            TicketSearchResponse response = toTicketSearchResponse(inventory, travelDate);
            if (response == null) {
                continue;
            }
            // 尝试从 Redis 读取更实时的可用票数，Redis 不可用时退回 DB 值
            enrichAvailableCount(response, inventory);
            result.add(response);
        }
        return result;
    }

    /** 如果 Redis 中有该库存的最新可用数，用它替换搜索结果中的 DB 值。 */
    private void enrichAvailableCount(TicketSearchResponse response, TicketInventory inventory) {
        try {
            Integer redisAvailable = redisInventoryService.getAvailableCount(inventory.getId());
            if (redisAvailable != null) {
                // Redis 有值：用更实时的 Redis 数值（可能已被并发下单扣减）
                response.setAvailableCount(redisAvailable);
            } else {
                // Redis key 不存在：把 DB 值写回 Redis 做懒加载
                redisInventoryService.saveInventory(inventory);
            }
        } catch (Exception e) {
            // Redis 不可用：保持 DB 原值，静默降级
        }
    }

    private TicketSearchResponse toTicketSearchResponse(TicketInventory inventory, LocalDate travelDate) {
        TrainDailySchedule schedule = scheduleMapper.selectById(inventory.getScheduleId());
        if (schedule == null || !travelDate.equals(schedule.getTravelDate()) || !"OPEN".equals(schedule.getStatus())) {
            return null;
        }

        Train train = trainMapper.selectById(schedule.getTrainId());
        if (train == null || !"ACTIVE".equals(train.getStatus())) {
            return null;
        }

        TrainStation departureStop = findTrainStation(train.getId(), inventory.getDepartureStationId());
        TrainStation arrivalStop = findTrainStation(train.getId(), inventory.getArrivalStationId());
        if (departureStop == null || arrivalStop == null || departureStop.getStopOrder() >= arrivalStop.getStopOrder()) {
            return null;
        }

        Station departureStation = stationMapper.selectById(inventory.getDepartureStationId());
        Station arrivalStation = stationMapper.selectById(inventory.getArrivalStationId());
        if (departureStation == null || arrivalStation == null) {
            return null;
        }

        Long durationMinutes = calculateDurationMinutes(departureStop.getDepartureTime(), arrivalStop.getArrivalTime());

        return new TicketSearchResponse(
                inventory.getId(),
                schedule.getId(),
                schedule.getTravelDate(),
                train.getId(),
                train.getTrainNo(),
                train.getTrainType(),
                departureStation.getId(),
                departureStation.getName(),
                arrivalStation.getId(),
                arrivalStation.getName(),
                departureStop.getDepartureTime(),
                arrivalStop.getArrivalTime(),
                durationMinutes,
                inventory.getSeatType(),
                inventory.getAvailableCount(),
                inventory.getPrice()
        );
    }

    private TrainStation findTrainStation(Long trainId, Long stationId) {
        return trainStationMapper.selectOne(new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainId, trainId)
                .eq(TrainStation::getStationId, stationId));
    }

    private Long calculateDurationMinutes(LocalTime departureTime, LocalTime arrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            return null;
        }
        long minutes = Duration.between(departureTime, arrivalTime).toMinutes();
        if (minutes < 0) {
            minutes += Duration.ofDays(1).toMinutes();
        }
        return minutes;
    }
}
