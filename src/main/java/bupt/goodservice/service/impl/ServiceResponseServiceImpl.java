package bupt.goodservice.service.impl;

import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceResponseServiceImpl implements ServiceResponseService {

    @Autowired
    private ServiceResponseMapper serviceResponseMapper;

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    @Override
    public ServiceResponse createServiceResponse(ServiceResponse serviceResponse) {
        serviceResponse.setCreatedAt(LocalDateTime.now());
        serviceResponse.setUpdatedAt(LocalDateTime.now());
        serviceResponse.setStatus(ServiceResponseStatus.PENDING);
        serviceResponseMapper.insert(serviceResponse);
        return serviceResponse;
    }

    @Override
    public ServiceResponse getServiceResponseById(Long id) {
        return serviceResponseMapper.findById(id);
    }

    @Override
    public List<ServiceResponse> getServiceResponsesByRequestId(Long requestId, int page, int size) {
        int offset = (page - 1) * size;
        return serviceResponseMapper.findByRequestId(requestId, offset, size);
    }

    @Override
    public List<ServiceResponse> getServiceResponsesByUserId(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return serviceResponseMapper.findByUserId(userId, offset, size);
    }

    @Override
    public List<ServiceResponse> getAllResponses(int page, int size) {
        int offset = (page - 1) * size;
        return serviceResponseMapper.findAll(offset, size);
    }

    @Override
    public ServiceResponse updateServiceResponse(Long id, ServiceResponse serviceResponse) {
        ServiceResponse existingResponse = serviceResponseMapper.findById(id);
        if (existingResponse == null) {
            throw new RuntimeException("ServiceResponse not found with id: " + id);
        }

        // A response can only be updated if it has not been accepted or rejected yet.
        if (existingResponse.getStatus() != ServiceResponseStatus.PENDING) {
            throw new IllegalStateException("Cannot update a response that has already been actioned upon.");
        }

        existingResponse.setDescription(serviceResponse.getDescription());
        existingResponse.setImageFiles(serviceResponse.getImageFiles());
        existingResponse.setUpdatedAt(LocalDateTime.now());
        // Potentially update status
        if (serviceResponse.getStatus() != null) {
            existingResponse.setStatus(serviceResponse.getStatus());
        }

        serviceResponseMapper.update(existingResponse);
        return existingResponse;
    }

    @Override
    public void deleteServiceResponse(Long id) {
        ServiceResponse existingResponse = serviceResponseMapper.findById(id);
        if (existingResponse == null) {
            throw new RuntimeException("ServiceResponse not found with id: " + id);
        }

        // A response can only be deleted if it has not been accepted or rejected yet.
        if (existingResponse.getStatus() != ServiceResponseStatus.PENDING) {
            throw new IllegalStateException("Cannot delete a response that has already been actioned upon.");
        }

        serviceResponseMapper.delete(id);
    }

    @Override
    public ServiceResponse updateResponseStatus(Long responseId, ServiceResponseStatus status, Long currentUserId) {
        ServiceResponse serviceResponse = serviceResponseMapper.findById(responseId);
        if (serviceResponse == null) {
            throw new RuntimeException("ServiceResponse not found with id: " + responseId);
        }

        ServiceRequest serviceRequest = serviceRequestMapper.findById(serviceResponse.getRequestId());
        if (serviceRequest == null) {
            throw new RuntimeException("Associated ServiceRequest not found");
        }

        // Check if the current user is the owner of the service request
        if (!serviceRequest.getUserId().equals(currentUserId)) {
            throw new SecurityException("Only the owner of the service request can accept or reject responses.");
        }

        // Further logic can be added here, e.g., only one response can be accepted.

        serviceResponse.setStatus(status);
        serviceResponse.setUpdatedAt(LocalDateTime.now());
        serviceResponseMapper.update(serviceResponse);

        return serviceResponse;
    }
}
