package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {

    User generateUUIDToken(String type, User user);


    String extractGoogleEmail(String token);

    boolean generateAcessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response);

    User validateAndGetUserByToken(String type, String token);
}
