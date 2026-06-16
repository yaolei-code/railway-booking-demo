package com.example.railway.user;

public record LoginResponse(
        Long id,
        String username,
        String status
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getStatus()
        );
    }
}
