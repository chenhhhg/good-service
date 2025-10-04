package bupt.goodservice.dto;

import bupt.goodservice.model.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequests {
    private Integer total;
    private List<ServiceRequest> data;
}
