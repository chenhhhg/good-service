package bupt.goodservice.service.impl;

import bupt.goodservice.dto.MonthlyStats;
import bupt.goodservice.mapper.StatsMapper;
import bupt.goodservice.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsMapper statsMapper;

    @Override
    public List<MonthlyStats> getMonthlyStats(YearMonth startMonth, YearMonth endMonth, String region, String sortBy, String sortOrder) {
        // You might want to set default values if they are null
        if (startMonth == null) {
            startMonth = YearMonth.now().minusMonths(6);
        }
        if (endMonth == null) {
            endMonth = YearMonth.now();
        }
        // Basic validation to prevent SQL injection for sortOrder
        if (sortOrder != null && !sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
            sortOrder = "asc"; // default to asc if invalid value is provided
        }
        
        return statsMapper.getMonthlyStats(startMonth, endMonth, region, sortBy, sortOrder);
    }
}
