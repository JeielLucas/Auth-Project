package com.auth.api.services;

import com.auth.api.dtos.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface JWTService {

    void authenticateUserFromToken(String token);

    ResponseEntity<ApiResponseDTO> validateAccessToken(HttpServletRequest request, HttpServletResponse response);
}
