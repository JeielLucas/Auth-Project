package com.auth.api.dtos;


public record ErrorResponseDTO<T>(int code, String message, T details){}
