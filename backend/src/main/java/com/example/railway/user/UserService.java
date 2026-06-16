package com.example.railway.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserResponse register(RegisterRequest request) {
        Long existingCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (existingCount > 0) {
            throw new IllegalArgumentException("username already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setStatus("ENABLED");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userMapper.insert(user);
        return UserResponse.from(user);
    }
}
