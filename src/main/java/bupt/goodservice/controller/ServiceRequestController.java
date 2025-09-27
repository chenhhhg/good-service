package bupt.goodservice.controller;

import bupt.goodservice.aspect.CheckOwnership;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(@RequestBody ServiceRequest serviceRequest) {
        ServiceRequest createdRequest = serviceRequestService.createServiceRequest(serviceRequest);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id) {
        ServiceRequest serviceRequest = serviceRequestService.getServiceRequestById(id);
        if (serviceRequest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(serviceRequest);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequest>> getAllRequests(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Long regionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests(serviceType, regionId, page, size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceRequest>> getRequestsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ServiceRequest> requests = serviceRequestService.getServiceRequestsByUserId(userId, page, size);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}")
    @CheckOwnership(resourceType = "ServiceRequest")
    public ResponseEntity<ServiceRequest> updateRequest(@PathVariable Long id, @RequestBody ServiceRequest serviceRequest) {
        ServiceRequest updatedRequest = serviceRequestService.updateServiceRequest(id, serviceRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    @DeleteMapping("/{id}")
    @CheckOwnership(resourceType = "ServiceRequest")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        serviceRequestService.deleteServiceRequest(id);
        return ResponseEntity.noContent().build();
    }
}
