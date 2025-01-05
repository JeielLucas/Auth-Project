package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import com.auth.api.services.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria um novo usuário no sistema",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            ref = "#/components/responses/successful_create"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            ref = "#/components/responses/invalid_credentials"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            ref = "#/components/responses/email_already_exists"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO> Register(@Valid @RequestBody RegisterRequestDTO user, HttpServletResponse response){
        return authServiceImpl.register(user, response);
    }


    @Operation(
            summary = "Login do usuário",
            description = "Realiza a autenticação do usuário. Retorna jwt de acesso e refresh nos cookies.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            ref = "#/components/responses/successful_login"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            ref = "#/components/responses/account_not_activated"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            ref = "#/components/responses/unauthorized"
                    ),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO> Login(@Valid @RequestBody LoginRequestDTO user, HttpServletResponse response){
        return authServiceImpl.login(user, response);
    }

    @Operation(
            summary = "Login do usuário utilizando o google",
            description = "Realiza a autenticação via google. Caso user não esteja cadastrado, leva para página de registro.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/successful_login"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/invalid_google_token"),
                    @ApiResponse(responseCode = "422", ref = "#/components/responses/google_login"),
            }
    )
    @PostMapping("/login/social/google")
    public ResponseEntity<ApiResponseDTO> loginWithGoogle(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.loginWithGoogle(token, response);
    }

    @Operation(
            summary = "Solicitação para reset de senha",
            description = "Manda o email para o usuário conseguir fazer o reset da sua senha.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            ref = "#/components/responses/reset_password"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            ref = "#/components/responses/unauthorized"

                    ),
                    @ApiResponse(
                            responseCode = "403",
                            ref = "#/components/responses/account_not_activated"
                    )
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO> forgotPassword(@RequestParam String email){
        return authServiceImpl.requestPasswordReset(email);
    }

    @Operation(
            summary = "Realiza a troca de senha do usuário.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/successful_reset_password"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/invalid_token"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/invalid_credentials"),
                    @ApiResponse(responseCode = "422", ref = "#/components/responses/reuse_password"),
            }
    )
    @PatchMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest password){
        return authServiceImpl.resetPassword(token, password);
    }


    @Operation(
            summary = "Ativa o usuário após o cadastro.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/activate"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/invalid_token"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/invalid_operation"),
            }
    )
    @PatchMapping("/activate")
    public ResponseEntity<ApiResponseDTO> activateUser(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.activateUser(token, response);
    }

    @Operation(
            summary = "Limpeza dos cookies para logout do usuário",
            responses = {
                    @ApiResponse(responseCode = "200", ref="#/components/responses/logout")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO> logout(HttpServletResponse response){
        return authServiceImpl.logout(response);
    }


    @Operation(
            summary = "Verifica validade do jwt.", description = "Verifica o access_token, caso não esteja correto, tenta validar o refresh_token.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/valid_token"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/not_valid_token")
            }
    )
    @GetMapping("/check")
    public ResponseEntity<ApiResponseDTO> checkAutentication(HttpServletRequest request, HttpServletResponse response){
        return authServiceImpl.checkAuth(request, response);
    }

    @Operation(
            summary = "Teste de conexão", description = "Testa a conexão com a api.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/ping")
            }
    )
    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

}
