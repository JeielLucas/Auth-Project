package com.auth.api.services;

import com.auth.api.entities.UserDetailsImpl;

public interface TokenProvider {
    String generateToken(UserDetailsImpl user, int durationInHours);

    String validateToken(String token);
}
