package com.auth.api.services;

import com.auth.api.entities.UserDetailsImpl;

public interface JWTService {
    String generateToken(UserDetailsImpl user, int durationInHours);

    String validateToken(String token);

    void authenticateUserFromToken(String token);
}
