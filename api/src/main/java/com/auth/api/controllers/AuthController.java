package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponse;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v2/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> Register(@Valid @RequestBody RegisterRequestDTO user){
        return authService.RegisterUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> Login(@Valid @RequestBody LoginRequestDTO user){
        return authService.LoginUser(user);
    }

    @GetMapping("/ativar-conta")
    public ResponseEntity activateUser(@RequestParam String token){
        return authService.activateUser(token);
    }

    @GetMapping("/reset-password")
    public ResponseEntity requestPasswordReset(@RequestParam String email){
        return authService.requestPasswordReset(email);
    }

    @GetMapping("/redefinir-senha")
    public ResponseEntity resetPassword(@RequestParam String token, @RequestBody String password){
        return authService.resetPassword(token, password);
    }

    @GetMapping("/validar-token")
    public ResponseEntity validarToken(@RequestParam String token){
        return authService.validarToken(token);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }
}
