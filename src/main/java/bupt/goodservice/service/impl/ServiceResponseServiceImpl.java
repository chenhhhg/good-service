package bupt.goodservice.service.impl;

import bupt.goodservice.dto.ServiceResponseDto;
import bupt.goodservice.dto.ServiceResponses;
import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.mapper.UserMapper;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.ServiceRequestStatus;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceResponseServiceImpl implements ServiceResponseService {

    @Autowired
    private ServiceResponseMapper serviceResponseMapper;

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    @Autowired
    private UserMapper userMapper;

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
    public ServiceResponses getServiceResponsesByRequestId(Long requestId, int page, int size) {
        int offset = (page - 1) * size;
        List<ServiceResponse> responses = serviceResponseMapper.findByRequestId(requestId, offset, size);
        Integer total = serviceResponseMapper.countByRequestId(requestId);
        return buildServiceResponses(responses, total);
    }

    @Override
    public ServiceResponses getServiceResponsesByUserId(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<ServiceResponse> responses = serviceResponseMapper.findByUserId(userId, offset, size);
        Integer total = serviceResponseMapper.countByUserId(userId);
        return buildServiceResponses(responses, total);
    }

    private ServiceResponses buildServiceResponses(List<ServiceResponse> responses, Integer total) {
        if (responses.isEmpty()) {
            return new ServiceResponses(0, List.of());
        }
        List<Long> requestIds = responses.stream().map(ServiceResponse::getRequestId).distinct().collect(Collectors.toList());
        List<ServiceRequest> requests = serviceRequestMapper.findByIds(requestIds);
        Map<Long, ServiceRequest> requestMap = requests.stream().collect(Collectors.toMap(ServiceRequest::getId, r -> r));

        List<ServiceResponseDto> dtoList = responses.stream().map(response -> {
            ServiceResponseDto dto = new ServiceResponseDto();
            dto.setId(response.getId());
            dto.setRequestId(response.getRequestId());
            dto.setUserId(response.getUserId());
            dto.setDescription(response.getDescription());
            dto.setImageFiles(response.getImageFiles());
            dto.setCreatedAt(response.getCreatedAt());
            dto.setUpdatedAt(response.getUpdatedAt());
            dto.setStatus(response.getStatus());
            dto.setRequest(requestMap.get(response.getRequestId()));
            return dto;
        }).collect(Collectors.toList());

        ServiceResponses built = new ServiceResponses(total, dtoList);

        HashMap<Long, List<ServiceResponseDto>> map = new HashMap<>();
        for (ServiceResponseDto dto : built.getData()) {
            map.computeIfAbsent(dto.getUserId(), d -> new ArrayList<>());
            map.get(dto.getUserId()).add(dto);
        }
        List<User> batchById = userMapper.findBatchById(map.keySet());
        for (User user : batchById) {
            user.setPassword(null);
            List<ServiceResponseDto> list = map.get(user.getId());
            for (ServiceResponseDto dto : list) {
                dto.setUser(user);
            }
        }
        return built;
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
        existingResponse.setImageFiles(serviceResponse.getImageFiles());
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
