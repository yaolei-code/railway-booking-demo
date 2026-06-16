package com.example.railway.ticket;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TicketInventoryMapper extends BaseMapper<TicketInventory> {

    @Update("""
            UPDATE ticket_inventory
            SET available_count = available_count - 1,
                locked_count = locked_count + 1
            WHERE id = #{inventoryId}
              AND available_count > 0
            """)
    int lockOneTicket(@Param("inventoryId") Long inventoryId);

    @Update("""
            UPDATE ticket_inventory
            SET locked_count = locked_count - 1
            WHERE schedule_id = #{scheduleId}
              AND departure_station_id = #{departureStationId}
              AND arrival_station_id = #{arrivalStationId}
              AND seat_type = #{seatType}
              AND locked_count > 0
            """)
    int confirmLockedTicket(
            @Param("scheduleId") Long scheduleId,
            @Param("departureStationId") Long departureStationId,
            @Param("arrivalStationId") Long arrivalStationId,
            @Param("seatType") String seatType
    );

    @Update("""
            UPDATE ticket_inventory
            SET available_count = available_count + 1,
                locked_count = locked_count - 1
            WHERE schedule_id = #{scheduleId}
              AND departure_station_id = #{departureStationId}
              AND arrival_station_id = #{arrivalStationId}
              AND seat_type = #{seatType}
              AND locked_count > 0
            """)
    int releaseLockedTicket(
            @Param("scheduleId") Long scheduleId,
            @Param("departureStationId") Long departureStationId,
            @Param("arrivalStationId") Long arrivalStationId,
            @Param("seatType") String seatType
    );
}
