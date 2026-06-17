package com.example.railway.user;

public record LoginResponse(
        Long id,
        String username,
        String role,
        String status,
        String token
) {
    public static LoginResponse from(User user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getStatus(),
                token
        );
    }
}
