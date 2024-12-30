package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.services.CookieServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/cookies")
public class CookiesController {

    private final CookieServiceImpl cookieServiceImpl;

    public CookiesController(CookieServiceImpl cookieServiceImpl) {
        this.cookieServiceImpl = cookieServiceImpl;
    }

    @GetMapping("/clear")
    public ResponseEntity<ApiResponseDTO> clearCookies(HttpServletResponse response) {
        return cookieServiceImpl.clearCookies(response);
    }
}
