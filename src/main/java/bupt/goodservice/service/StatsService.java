package bupt.goodservice.service;

import bupt.goodservice.dto.MonthlyStats;
import java.time.YearMonth;
import java.util.List;

public interface StatsService {
    List<MonthlyStats> getMonthlyStats(YearMonth startMonth, YearMonth endMonth, String region, String sortBy, String sortOrder);
}
