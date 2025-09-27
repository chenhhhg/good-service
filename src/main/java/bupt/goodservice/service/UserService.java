package bupt.goodservice.service;

import bupt.goodservice.dto.LoginRequest;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.dto.UserUpdateRequest;
import bupt.goodservice.model.User;

import java.util.List;

public interface UserService {
    User register(RegisterRequest registerRequest);
    String login(LoginRequest loginRequest);
    User findByUsername(String username);
    User updateUser(String username, UserUpdateRequest userUpdateRequest);
    List<User> getAllUsers(int page, int size);
}
