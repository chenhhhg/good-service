package bupt.goodservice.service;

import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.enums.ServiceResponseStatus;

import java.util.List;

public interface ServiceResponseService {
    ServiceResponse createServiceResponse(ServiceResponse serviceResponse);
    ServiceResponse getServiceResponseById(Long id);
    List<ServiceResponse> getServiceResponsesByRequestId(Long requestId, int page, int size);
    List<ServiceResponse> getServiceResponsesByUserId(Long userId, int page, int size);
    List<ServiceResponse> getAllResponses(int page, int size);
    ServiceResponse updateServiceResponse(Long id, ServiceResponse serviceResponse);
    void deleteServiceResponse(Long id);

    ServiceResponse acceptOrRejectResponse(Long responseId, Long requestId, ServiceResponseStatus status, Long currentUserId);
}
