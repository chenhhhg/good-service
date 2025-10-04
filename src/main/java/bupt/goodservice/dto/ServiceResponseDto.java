package bupt.goodservice.dto;

import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceResponseDto extends ServiceResponse {
    private ServiceRequest request;
    private User user;
}
