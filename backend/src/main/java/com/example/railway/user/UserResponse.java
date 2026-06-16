package com.example.railway.user;

public record UserResponse(
        Long id,
        String username,
        String phone,
        String email,
        String status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus()
        );
    }
}
