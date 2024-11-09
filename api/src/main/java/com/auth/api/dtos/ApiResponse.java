package com.auth.api.dtos;


public record ApiResponse<T>(boolean success, T data, String message){}
