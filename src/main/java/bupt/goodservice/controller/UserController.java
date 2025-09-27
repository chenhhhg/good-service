package bupt.goodservice.controller;

import bupt.goodservice.dto.UserUpdateRequest;
import bupt.goodservice.model.User;
import bupt.goodservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest, Principal principal) {
        User updatedUser = userService.updateUser(principal.getName(), userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
