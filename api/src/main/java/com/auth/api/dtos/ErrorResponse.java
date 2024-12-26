package com.auth.api.dtos;


import java.util.List;

public record ErrorResponse<T>(int code, String message, T details){}
