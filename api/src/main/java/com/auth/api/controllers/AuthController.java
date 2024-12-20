package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponse;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import com.auth.api.services.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v2/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    public AuthController(final AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> Register(@Valid @RequestBody RegisterRequestDTO user){
        return authServiceImpl.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> Login(@Valid @RequestBody LoginRequestDTO user, HttpServletResponse response){
        return authServiceImpl.login(user, response);
    }

    @PostMapping("/e")
    public ResponseEntity<ApiResponse> googleLogin(){
        return authServiceImpl.googleLogin("1");
    }

    @PostMapping("/reset-password")
    public ResponseEntity requestPasswordReset(@RequestParam String email){
        return authServiceImpl.requestPasswordReset(email);
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest password){
        return authServiceImpl.resetPassword(token, password);
    }

    @GetMapping("/ativar-conta")
    public ResponseEntity activateUser(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.activateUser(token, response);
    }

    @GetMapping("/validar-token")
    public ResponseEntity validarToken(HttpServletRequest request){
        return authServiceImpl.validarToken(request);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }
}
