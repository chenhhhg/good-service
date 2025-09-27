package bupt.goodservice.model;

import bupt.goodservice.model.enums.ServiceRequestStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceRequest {
    private Long id;
    private Long userId;
    private Long regionId;
    private String serviceType;
    private String title;
    private String description;
    private String imageFiles; // 逗号分隔文件名
    private String videoFile; // 逗号分隔文件名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ServiceRequestStatus status;
}
