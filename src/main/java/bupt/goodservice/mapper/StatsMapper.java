package bupt.goodservice.mapper;

import bupt.goodservice.dto.MonthlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.YearMonth;
import java.util.List;

@Mapper
public interface StatsMapper {

    List<MonthlyStats> getMonthlyStats(
            @Param("startMonth") YearMonth startMonth,
            @Param("endMonth") YearMonth endMonth,
            @Param("region") String region, // Assuming region is identified by name for simplicity
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder
    );
}
