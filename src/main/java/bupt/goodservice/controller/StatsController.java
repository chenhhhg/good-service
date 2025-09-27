package bupt.goodservice.controller;

import bupt.goodservice.dto.MonthlyStats;
import bupt.goodservice.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyStats>> getMonthlyStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        // In a real app, an AOP aspect or Spring Security would check for ADMIN role here.
        
        List<MonthlyStats> stats = statsService.getMonthlyStats(startMonth, endMonth, region, sortBy, sortOrder);
        return ResponseEntity.ok(stats);
    }
}
