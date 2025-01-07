package com.auth.api.services;

import com.auth.api.entities.User;

public interface TokenService {

    void generateUUIDToken(String type, User user);

    String extractGoogleEmail(String token);

    User validateAndGetUserByToken(String type, String token);
}
