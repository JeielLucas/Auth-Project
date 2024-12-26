package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponse;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import com.auth.api.entities.User;
import com.auth.api.repositories.UserRepository;
import com.auth.api.services.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v2/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    private final AuthServiceImpl authServiceImpl;

    public AuthController(final AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> Register(@Valid @RequestBody RegisterRequestDTO user){
        return authServiceImpl.register(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Realiza a autenticação do usuário com email e senha")
    public ResponseEntity<ApiResponse> Login(@Valid @RequestBody LoginRequestDTO user, HttpServletResponse response){
        return authServiceImpl.login(user, response);
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse> loginWithGoogle(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.loginWithGoogle(token, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email){
        return authServiceImpl.requestPasswordReset(email);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest password){
        return authServiceImpl.resetPassword(token, password);
    }

    @PatchMapping("/users/activate")
    public ResponseEntity<ApiResponse> activateUser(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.activateUser(token, response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(HttpServletRequest request, HttpServletResponse response){
        return authServiceImpl.validateToken(request, response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    //Retirar isso depois
    @GetMapping("/users")
    public List<User> list(){
        return userRepository.findAll();
    }
}
