package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {

    String generateToken(UserDetailsImpl user, int durationInHours);

    String validateToken(String token);

    void generateJWTandAddCookiesToResponse(User user, HttpServletResponse response, String name, int maxAge, boolean secure, boolean httpOnly, int duration);

}
