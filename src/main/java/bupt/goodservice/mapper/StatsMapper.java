package bupt.goodservice.mapper;

import bupt.goodservice.dto.MonthlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StatsMapper {

    List<MonthlyStats> getMonthlyStatsTotal(
            @Param("first") LocalDateTime first,
            @Param("last") LocalDateTime last,
            @Param("region") String region,
            @Param("success") boolean success
    );
}
