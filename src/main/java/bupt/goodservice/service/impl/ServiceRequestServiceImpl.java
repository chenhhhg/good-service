package bupt.goodservice.service.impl;

import bupt.goodservice.mapper.RegionalDivisionsMapper;
import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.model.RegionalDivision;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.enums.ServiceRequestStatus;
import bupt.goodservice.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    @Autowired
    private ServiceResponseMapper serviceResponseMapper;

    @Autowired
    private RegionalDivisionsMapper regionalDivisionsMapper;

    @Override
    public ServiceRequest createServiceRequest(ServiceRequest serviceRequest) {
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setUpdatedAt(LocalDateTime.now());
        serviceRequest.setStatus(ServiceRequestStatus.PUBLISHED);
        serviceRequestMapper.insert(serviceRequest);
        return serviceRequest;
    }

    @Override
    public ServiceRequest getServiceRequestById(Long id) {
        return serviceRequestMapper.findById(id);
    }

    @Override
    public List<ServiceRequest> getAllServiceRequests(String serviceType, Long regionId, int page, int size) {
        int offset = (page - 1) * size;
        return serviceRequestMapper.findAll(serviceType, regionId, offset, size);
    }
    
    @Override
    public List<ServiceRequest> getServiceRequestsByUserId(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return serviceRequestMapper.findByUserId(userId, offset, size);
    }

    @Override
    public ServiceRequest updateServiceRequest(Long id, ServiceRequest serviceRequest) {
        ServiceRequest existingRequest = serviceRequestMapper.findById(id);
        if (existingRequest == null) {
            throw new RuntimeException("ServiceRequest not found with id: " + id);
        }
        
        // A request can only be updated if no one has responded to it yet.
        List<ServiceResponse> responses = serviceResponseMapper.findByRequestId(id, 0, 1);
        if (!responses.isEmpty()) {
            throw new IllegalStateException("Cannot update a service request that already has responses.");
        }
        
        existingRequest.setTitle(serviceRequest.getTitle());
        existingRequest.setDescription(serviceRequest.getDescription());
        existingRequest.setUpdatedAt(LocalDateTime.now());
        
        serviceRequestMapper.update(existingRequest);
        return existingRequest;
    }

    @Override
    public void deleteServiceRequest(Long id) {
         ServiceRequest existingRequest = serviceRequestMapper.findById(id);
        if (existingRequest == null) {
            throw new RuntimeException("ServiceRequest not found with id: " + id);
        }
        
        // A request can only be deleted if no one has responded to it yet.
        List<ServiceResponse> responses = serviceResponseMapper.findByRequestId(id, 0, 1);
        if (!responses.isEmpty()) {
            throw new IllegalStateException("Cannot delete a service request that already has responses.");
        }

        existingRequest.setStatus(ServiceRequestStatus.CANCELLED);
        serviceRequestMapper.update(existingRequest);
//        serviceRequestMapper.delete(id);
    }

    @Override
    public List<RegionalDivision> getAllRegions() {
        return regionalDivisionsMapper.selectAllEntity();
    }

    @Override
    public Integer getAllServiceRequestsCount(String serviceType, Long regionId) {
        return serviceRequestMapper.countAll(serviceType, regionId);
    }
}
