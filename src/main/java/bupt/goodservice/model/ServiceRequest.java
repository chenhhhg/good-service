package bupt.goodservice.model;

import bupt.goodservice.model.enums.ServiceRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceRequest {
    private Long id;
    @NotNull
    private Long userId;
    private User user;
    @NotNull(message = "必须填写区域")
    private Long regionId;
    @NotBlank(message = "必须填写服务类型")
    private String serviceType;
    @NotBlank(message = "标题不能为空")
    @Size(max = 30, message = "标题不长于30个字")
    private String title;
    private String description;
    private String imageFiles; // 逗号分隔文件名
    private String videoFile; // 逗号分隔文件名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ServiceRequestStatus status;
}
