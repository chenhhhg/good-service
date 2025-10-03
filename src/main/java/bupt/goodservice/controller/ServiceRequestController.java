package bupt.goodservice.controller;

import bupt.goodservice.aspect.CheckOwnership;
import bupt.goodservice.model.RegionalDivision;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.enums.ServiceType;
import bupt.goodservice.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(@Valid @RequestBody ServiceRequest serviceRequest) {
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

    @GetMapping("/types")
    public ResponseEntity<String[]> getAllServiceTypes() {
        String[] names = ServiceType.getAllChineseNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/regions")
    public ResponseEntity<Map<String, Map<String, Map<String, String>>>> getAllRegions() {
        List<RegionalDivision> r = serviceRequestService.getAllRegions();
        Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
        for (RegionalDivision region : r) {
            String provinceName = region.getProvinceName();
            map.computeIfAbsent(provinceName, s -> new HashMap<>());
            Map<String, Map<String, String>> pMap = map.get(provinceName);
            String cityName = region.getCityName();
            pMap.computeIfAbsent(cityName, s -> new HashMap<>());
            Map<String, String> cMap = pMap.get(cityName);
            String regionalName = region.getRegionalName();
            cMap.put(regionalName, region.getRegionalCode());
        }
        return ResponseEntity.ok(map);
    }
}
