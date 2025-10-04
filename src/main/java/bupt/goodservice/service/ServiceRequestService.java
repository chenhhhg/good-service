package bupt.goodservice.service;

import bupt.goodservice.model.RegionalDivision;
import bupt.goodservice.model.ServiceRequest;

import java.util.List;

public interface ServiceRequestService {
    ServiceRequest createServiceRequest(ServiceRequest serviceRequest);
    ServiceRequest getServiceRequestById(Long id);
    List<ServiceRequest> getAllServiceRequests(String serviceType, Long regionId, int page, int size);
    List<ServiceRequest> getServiceRequestsByUserId(Long userId, int page, int size);
    ServiceRequest updateServiceRequest(Long id, ServiceRequest serviceRequest);
    void deleteServiceRequest(Long id);

    List<RegionalDivision> getAllRegions();

    Integer getAllServiceRequestsCount(String serviceType, Long regionId);
}
