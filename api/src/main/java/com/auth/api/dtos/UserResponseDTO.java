package com.auth.api.dtos;

import com.auth.api.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponseDTO(String email, UserRole role, LocalDateTime createdAt) {
}
