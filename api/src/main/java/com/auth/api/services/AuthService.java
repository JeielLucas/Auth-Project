package com.auth.api.services;

import com.auth.api.dtos.ApiResponse;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface AuthService {

    ResponseEntity<ApiResponse> login(LoginRequestDTO loginRequestDTO, HttpServletResponse httpServletResponse);

    ResponseEntity<ApiResponse> loginWithGoogle(String token, HttpServletResponse httpServletResponse);

    ResponseEntity<ApiResponse> register(RegisterRequestDTO registerRequestDTO);

    ResponseEntity<ApiResponse> activateUser(String token, HttpServletResponse response);

    ResponseEntity<ApiResponse> requestPasswordReset(String email);

    ResponseEntity<ApiResponse> resetPassword(String token, ResetPasswordRequest passwordRequest);

    ResponseEntity<ApiResponse> validateToken(HttpServletRequest request, HttpServletResponse response);

}
