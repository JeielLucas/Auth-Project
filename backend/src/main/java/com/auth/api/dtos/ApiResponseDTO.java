package com.auth.api.dtos;


public record ApiResponseDTO<T>(boolean success, T data, String message){}
