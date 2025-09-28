package bupt.goodservice.dto;

import bupt.goodservice.model.RegionalDivisions;
import lombok.Data;

@Data
public class MonthlyStats {
    private String month; // YYYY-MM
    private RegionalDivisions region;
    private String serviceType;
    private Long requestCount;
    private Long successCount;
}
