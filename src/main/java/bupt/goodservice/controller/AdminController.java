package bupt.goodservice.controller;

import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import bupt.goodservice.service.ServiceRequestService;
import bupt.goodservice.service.ServiceResponseService;
import bupt.goodservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceResponseService serviceResponseService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Admin role check needed
        List<User> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequest>> getAllRequests(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Long regionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Admin role check needed
        List<ServiceRequest> requests = serviceRequestService.getAllServiceRequests(serviceType, regionId, page, size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/responses")
    public ResponseEntity<List<ServiceResponse>> getAllResponses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Admin role check needed
        List<ServiceResponse> responses = serviceResponseService.getAllResponses(page, size);
        return ResponseEntity.ok(responses);
    }
}
