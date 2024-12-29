package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import com.auth.api.services.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Registro do usuário", description = "Cadastra um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Sucesso",
                                    value = """
                        {
                          "sucess": "true",
                          "data": {
                                    "email": "email@gmail.com",
                                    "role": "USER.ROLE",
                                    "createdAt": "2024-12-29T16:20:06.976997263"
                          },
                          "message": "Usuário criado com sucesso"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Emails e/ou senhas são diferentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Erro",
                                    value = """
                        {
                          "code": 400,
                          "message": "Senhas não coincidem",
                          "details": [
                                       "Senhas não coincidem"
                          ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Emails e/ou senhas são diferentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Erro",
                                    value = """
                        {
                          "code": 409,
                          "message": "Email já cadastrado",
                          "details": [
                                       "Email teste@gmail.com já está cadastrado"
                          ]
                        }
                        """
                            )
                    )
            ),
    })
    public ResponseEntity<ApiResponseDTO> Register(@Valid @RequestBody RegisterRequestDTO user){
        return authServiceImpl.register(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Realiza a autenticação do usuário. Retorna jwt de acesso e refresh nos cookies.")
    public ResponseEntity<ApiResponseDTO> Login(@Valid @RequestBody LoginRequestDTO user, HttpServletResponse response){
        return authServiceImpl.login(user, response);
    }

    @PostMapping("/login/google")
    @Operation(summary = "Login do usuário utilizando o google", description = "Realiza a autenticação via google. Caso user não esteja cadastrado, leva para página de registro.")
    public ResponseEntity<ApiResponseDTO> loginWithGoogle(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.loginWithGoogle(token, response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitação para reset de senha", description = "Manda o email para o usuário conseguir fazer o reset da sua senha.")
    public ResponseEntity<ApiResponseDTO> forgotPassword(@RequestParam String email){
        return authServiceImpl.requestPasswordReset(email);
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Realiza a troca de senha do usuário.")
    public ResponseEntity<ApiResponseDTO> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest password){
        return authServiceImpl.resetPassword(token, password);
    }

    @PatchMapping("/users/activate")
    @Operation(summary = "Ativa o usuário após o cadastro.")
    public ResponseEntity<ApiResponseDTO> activateUser(@RequestParam String token, HttpServletResponse response){
        return authServiceImpl.activateUser(token, response);
    }

    @GetMapping("/check")
    @Operation(summary = "Verifica validade do jwt.", description = "Verifica o access_token, caso não esteja correto, tenta validar o refresh_token.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token validado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Sucesso",
                                    value = """
                        {
                          "id": "123",
                          "username": "usuario123",
                          "email": "usuario@example.com",
                          "createdAt": "2024-12-29T10:00:00Z"
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token não validado, usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Erro",
                                    value = """
                        {
                          "id": "123",
                          "username": "usuario123",
                          "email": "usuario@example.com",
                          "createdAt": "2024-12-29T10:00:00Z"
                        }
                        """
                            )
                    )
            )
    })
    public ResponseEntity<Void> checkAutentication(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ping")
    @Operation(summary = "Teste de conexão", description = "Testa a conexão com a api.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conexão feita com sucesso",
                    content = @Content(
                            examples = @ExampleObject(

                                    value = """
                        {
                          "pong"
                        }
                        """
                            )
                    )
            )
    })
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

}
