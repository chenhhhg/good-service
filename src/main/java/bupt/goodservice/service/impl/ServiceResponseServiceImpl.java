package bupt.goodservice.service.impl;

import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.enums.ServiceRequestStatus;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        existingResponse.setStatus(ServiceResponseStatus.CANCELLED);
        serviceResponseMapper.update(existingResponse);
//        serviceResponseMapper.delete(id);
    }

    @Override
    @Transactional
    public ServiceResponse acceptOrRejectResponse(Long responseId, Long requestId, ServiceResponseStatus status, Long currentUserId) {
        //锁上需求
        ServiceRequest serviceRequest = serviceRequestMapper.findByIdForUpdate(requestId);
        if (serviceRequest == null) {
            throw new RuntimeException("需求不存在");
        }
        ServiceResponse serviceResponse = serviceResponseMapper.findById(responseId);
        if (serviceResponse == null) {
            throw new RuntimeException("响应不存在: " + responseId);
        }
        if (!serviceRequest.getUserId().equals(currentUserId)) {
            throw new SecurityException("仅需求拥有者可接受或拒绝");
        }
        if (serviceRequest.getStatus() != ServiceRequestStatus.PUBLISHED) {
            throw new RuntimeException("该需求已完成或已取消，无法修改！");
        }
        if (serviceResponse.getStatus() != ServiceResponseStatus.PENDING) {
            throw new RuntimeException("该响应已拒绝或已取消，无法修改！");
        }
        // accept or reject
        serviceResponse.setStatus(status);
        serviceResponse.setUpdatedAt(LocalDateTime.now());
        serviceResponseMapper.update(serviceResponse);

        if (status == ServiceResponseStatus.ACCEPTED) {
            serviceRequest.setStatus(ServiceRequestStatus.COMPLETED);
            serviceRequestMapper.update(serviceRequest);
        }

        return serviceResponse;
    }
}
