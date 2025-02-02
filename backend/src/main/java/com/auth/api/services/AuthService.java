package com.auth.api.services;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<ApiResponseDTO> login(LoginRequestDTO loginRequestDTO, HttpServletResponse httpServletResponse);

    ResponseEntity<ApiResponseDTO> loginWithGoogle(String token, HttpServletResponse httpServletResponse);

    ResponseEntity<ApiResponseDTO> register(RegisterRequestDTO registerRequestDTO, HttpServletResponse response);

    ResponseEntity<ApiResponseDTO> activateUser(String token, HttpServletResponse response);

    ResponseEntity<ApiResponseDTO> requestPasswordReset(String email);

    ResponseEntity<ApiResponseDTO> resetPassword(String token, ResetPasswordRequest passwordRequest);

    ResponseEntity<ApiResponseDTO> logout(HttpServletResponse response);

    ResponseEntity<ApiResponseDTO> checkAuth(HttpServletRequest request, HttpServletResponse response);

}
