package bupt.goodservice.dto;

import lombok.Data;

@Data
public class MonthlyStats {
    private String month; // YYYY-MM
    private String region;
    private String serviceType;
    private long requestCount;
    private long successCount;
}
