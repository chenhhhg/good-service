package bupt.goodservice.controller;

import bupt.goodservice.aspect.CheckOwnership;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
public class ServiceResponseController {

    @Autowired
    private ServiceResponseService serviceResponseService;

    @PostMapping
    public ResponseEntity<ServiceResponse> createResponse(@RequestBody ServiceResponse serviceResponse) {
        // userId should be extracted from security context
        ServiceResponse createdResponse = serviceResponseService.createServiceResponse(serviceResponse);
        return ResponseEntity.ok(createdResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getResponseById(@PathVariable Long id) {
        ServiceResponse serviceResponse = serviceResponseService.getServiceResponseById(id);
        if (serviceResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ServiceResponse>> getResponsesByRequestId(
            @PathVariable Long requestId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ServiceResponse> responses = serviceResponseService.getServiceResponsesByRequestId(requestId, page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceResponse>> getResponsesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ServiceResponse> responses = serviceResponseService.getServiceResponsesByUserId(userId, page, size);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @CheckOwnership(resourceType = "ServiceResponse")
    public ResponseEntity<ServiceResponse> updateResponse(@PathVariable Long id, @RequestBody ServiceResponse serviceResponse) {
        ServiceResponse updatedResponse = serviceResponseService.updateServiceResponse(id, serviceResponse);
        return ResponseEntity.ok(updatedResponse);
    }

    @DeleteMapping("/{id}")
    @CheckOwnership(resourceType = "ServiceResponse")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        serviceResponseService.deleteServiceResponse(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate,
            HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        ServiceResponseStatus newStatus = ServiceResponseStatus.valueOf(statusUpdate.get("status").toUpperCase());
        
        ServiceResponse updatedResponse = serviceResponseService.updateResponseStatus(id, newStatus, currentUserId);
        return ResponseEntity.ok(updatedResponse);
    }
}
