package bupt.goodservice.model;

import bupt.goodservice.model.enums.ServiceResponseStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceResponse {
    private Long id;
    private Long requestId;
    private Long userId;
    private String description;
    private String imageFiles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ServiceResponseStatus status;
}
