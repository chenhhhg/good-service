package bupt.goodservice.controller;

import bupt.goodservice.dto.UserUpdateRequest;
import bupt.goodservice.model.User;
import bupt.goodservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        User user = userService.findByUsername(String.valueOf(request.getAttribute("username")));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        User updatedUser = userService.updateUser(String.valueOf(request.getAttribute("username")), userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
