package bupt.goodservice.controller;

import bupt.goodservice.aspect.CheckIfAdmin;
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
    @CheckIfAdmin
    public ResponseEntity<List<MonthlyStats>> getMonthlyStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth,
            @RequestParam(required = false) String region,
            @RequestParam(required = false, defaultValue = "false") boolean success) {
        List<MonthlyStats> stats = statsService.getMonthlyStats(startMonth, endMonth, region, success);

        return ResponseEntity.ok(stats);
    }
}
