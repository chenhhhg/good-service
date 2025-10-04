package bupt.goodservice.controller;

import bupt.goodservice.aspect.CheckOwnership;
import bupt.goodservice.dto.ServiceRequests;
import bupt.goodservice.model.RegionalDivision;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.ServiceType;
import bupt.goodservice.service.ServiceRequestService;
import bupt.goodservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;
    @Autowired
    private UserService userService;

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
        User user = userService.getById(serviceRequest.getUserId());
        user.setPassword(null);
        serviceRequest.setUser(user);
        return ResponseEntity.ok(serviceRequest);
    }

    @GetMapping
    public ResponseEntity<ServiceRequests> getAllRequests(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Long regionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests(serviceType, regionId, page, size);
        Map<Long, List<ServiceRequest>> map = requests.stream()
                .collect(Collectors.groupingBy(ServiceRequest::getUserId));
        List<User> users = userService.getBatchById(map.keySet());
        for (User user : users) {
            user.setPassword(null);
            for (ServiceRequest request : map.get(user.getId())) {
                request.setUser(user);
            }
        }
        ServiceRequests serviceRequests = new ServiceRequests();
        serviceRequests.setData(requests);
        Integer cnt = serviceRequestService.getAllServiceRequestsCount(serviceType, regionId);
        return ResponseEntity.ok(serviceRequests);
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
    public ResponseEntity<Map<String, Map<String, Map<String, Integer>>>> getAllRegions() {
        List<RegionalDivision> r = serviceRequestService.getAllRegions();
        Map<String, Map<String, Map<String, Integer>>> map = new HashMap<>();
        for (RegionalDivision region : r) {
            String provinceName = region.getProvinceName();
            map.computeIfAbsent(provinceName, s -> new HashMap<>());
            Map<String, Map<String, Integer>> pMap = map.get(provinceName);
            String cityName = region.getCityName();
            pMap.computeIfAbsent(cityName, s -> new HashMap<>());
            Map<String, Integer> cMap = pMap.get(cityName);
            String regionalName = region.getRegionalName();
            cMap.put(regionalName, region.getId());
        }
        return ResponseEntity.ok(map);
    }


}
