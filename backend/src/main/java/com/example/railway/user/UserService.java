package com.example.railway.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.railway.common.UnauthorizedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper, JwtService jwtService) {
        this.userMapper = userMapper;
        this.jwtService = jwtService;
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

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (user == null) {
            throw new IllegalArgumentException("username or password is incorrect");
        }

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash());
        if (!passwordMatches) {
            throw new IllegalArgumentException("username or password is incorrect");
        }

        if (!"ENABLED".equals(user.getStatus())) {
            throw new IllegalArgumentException("user account is disabled");
        }

        String token = jwtService.createToken(user);
        return LoginResponse.from(user, token);
    }

    public UserResponse getCurrentUser(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        Long userId = jwtService.parseUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null || !"ENABLED".equals(user.getStatus())) {
            throw new UnauthorizedException("user is not logged in");
        }
        return UserResponse.from(user);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("missing token");
        }
        return authorizationHeader.substring("Bearer ".length());
    }
}
