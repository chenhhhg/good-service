package bupt.goodservice.dto;

import bupt.goodservice.model.RegionalDivision;
import lombok.Data;

@Data
public class MonthlyStats {
    private String month; // YYYY-MM
    private RegionalDivision region;
    private String serviceType;
    private Long requestCount;
    private Long successCount;
}
