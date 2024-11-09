package com.auth.api.dtos;


import java.util.List;

public record ErrorResponse(int code, String message, List<String> details){}
