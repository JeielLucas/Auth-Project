package com.auth.api.services;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface CookieService {

    void generateJWTandAddCookiesToResponse(User user, HttpServletResponse response, String name, int maxAge, boolean secure, boolean httpOnly, int duration);

    String findCookieValue(HttpServletRequest request, String name);

    ResponseEntity<ApiResponseDTO> clearCookies(HttpServletResponse response);
}
