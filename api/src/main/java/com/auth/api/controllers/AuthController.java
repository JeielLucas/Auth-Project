package com.auth.api.controllers;

import com.auth.api.repositories.LoginRequestDTO;
import com.auth.api.repositories.RegisterRequestDTO;
import com.auth.api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> Register(@Valid @RequestBody RegisterRequestDTO user){
        return authService.RegisterUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> Login(@Valid @RequestBody LoginRequestDTO user){
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
