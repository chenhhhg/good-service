package bupt.goodservice.service.impl;

import bupt.goodservice.dto.MonthlyStats;
import bupt.goodservice.mapper.StatsMapper;
import bupt.goodservice.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsMapper statsMapper;

    @Override
    public List<MonthlyStats> getMonthlyStats(YearMonth startMonth, YearMonth endMonth, String region, boolean success) {
        if (startMonth == null) {
            startMonth = YearMonth.now().minusMonths(6);
        }
        if (endMonth == null) {
            endMonth = YearMonth.now();
        }
        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = endMonth.atEndOfMonth().atTime(23, 59, 59, 999000000);
        return statsMapper.getMonthlyStatsTotal(start, end, region, success);
    }
}
