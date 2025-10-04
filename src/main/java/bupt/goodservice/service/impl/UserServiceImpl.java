package bupt.goodservice.service.impl;

import bupt.goodservice.dto.LoginRequest;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.dto.UserUpdateRequest;
import bupt.goodservice.mapper.UserMapper;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.UserType;
import bupt.goodservice.service.UserService;
import bupt.goodservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest registerRequest) {
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        user.setUserType(UserType.USER);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(LoginRequest loginRequest) {
        User user = userMapper.findByUsername(loginRequest.getUsername());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户不存在或密码错误！");
        }

        return jwtUtils.generateToken(user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User updateUser(String username, UserUpdateRequest userUpdateRequest) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (userUpdateRequest.getPhone() != null) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        if (userUpdateRequest.getProfile() != null) {
            user.setProfile(userUpdateRequest.getProfile());
        }
        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        user.setUpdatedTime(LocalDateTime.now());

        userMapper.update(user);
        return user;
    }

    @Override
    public List<User> getAllUsers(int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.findAll(offset, size);
    }

    @Override
    public User getById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public List<User> getBatchById(Set<Long> ids) {
        return userMapper.findBatchById(ids);
    }
}
