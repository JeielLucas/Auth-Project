package com.auth.api.dtos;


import com.auth.api.enums.UserRole;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

@Component
public class ApiResponses {

    public static final ApiResponse SUCCESSFUL_CREATE = new ApiResponse().description("Usuário criado com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, new UserResponseDTO("user@gmail.com", UserRole.USER, LocalDateTime.now()), "Usuário criado com sucesso")
                            )));

    public static final ApiResponse INVALID_CREDENTIALS = new ApiResponse().description("Emails e/ou senhas inválidos")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(400,  "Emails e/ou senhas inválidos", Collections.singletonList("Emails e/ou senhas não coincidem"))
                            )));

    public static final ApiResponse EMAIL_ALREADY_EXISTS = new ApiResponse().description("Usuário já cadastrado")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(409,  "Usuário já cadastrado", Collections.singletonList("Usuário já cadastrado"))
                            )));

    public static final ApiResponse UNAUTHORIZED = new ApiResponse().description("Credenciais invalidas")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(401, "Credenciais invalidas", Collections.singletonList("Credenciais invalidas"))
                            )));

    public static final ApiResponse SUCCESSFUL_LOGIN = new ApiResponse().description("Login realizado com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "", "Login efetuado com sucesso")
                            )));

    public static final ApiResponse ACCOUNT_NOT_ACTIVATED = new ApiResponse().description("Conta não ativa")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(403, "Usuário inativo, por favor, verifique seu email", Collections.singletonList("Usuário inativo, por favor, verifique seu email"))
                            )));

    public static final ApiResponse PING = new ApiResponse().description("Teste de conexão")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    "pong"
                            )));


    public static final ApiResponse VALID_TOKEN = new ApiResponse().description("Token validado com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "email@gmail.com", "Token validado com sucesso")
                            )));


    public static final ApiResponse NOT_VALID_TOKEN = new ApiResponse().description("Token inválido ou expirado")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(401, "Token inválido ou expirado", Collections.singletonList("Refresh token invalido ou expirado, faca o login novamente"))
                            )));

    public static final ApiResponse ACTIVATE_ACCOUNT = new ApiResponse().description("Conta ativada com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "", "Conta ativada com sucesso")
                            )));

    public static final ApiResponse INVALID_TOKEN = new ApiResponse().description("Token inválido ou expirado")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(409, "Token invalido ou expirado", Collections.singletonList("Token invalido ou expirado"))
                            )));

    public static final ApiResponse INVALID_OPERATION = new ApiResponse().description("Usuário já ativo")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(400, "Usuário já está ativo", Collections.singletonList("Usuário já está ativo"))
                            )));

    public static final ApiResponse REDIRECT_GOOGLE_LOGIN = new ApiResponse().description("Usuário não cadastrado")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(false, map("email@gmail.com"), "Usuário precisa se cadastrar")
                            )
                    )
            ).description("Redireciona para página de cadastro");


    public static final ApiResponse INVALID_GOOGLE_TOKEN = new ApiResponse().description("Token inválido")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(409, "Token invalido", Collections.singletonList("Token invalido"))
                            )));

    public static final ApiResponse RESET_PASSWORD = new ApiResponse().description("Envio de email para reset de senha")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "", "Verifique seu email")
                            )));

    public static final ApiResponse REUSE_PASSWORD = new ApiResponse().description("Nova senha não pode ser igual a atual")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ErrorResponseDTO<>(422, "A nova senha não pode ser igual a antiga", Collections.singletonList("A nova senha não pode ser igual a antiga"))
                            )));

    public static final ApiResponse SUCCESSFUL_RESET_PASSWORD = new ApiResponse().description("Senha alterada com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "", "Senha redefinida com sucesso")
                            )));

    public static final ApiResponse LOGOUT = new ApiResponse().description("Logout realizado com sucesso")
            .content(new Content()
                    .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                            new io.swagger.v3.oas.models.media.MediaType().example(
                                    new ApiResponseDTO<>(true, "", "Cookies limpos com sucesso")
                            )));

    private static HashMap<String, String> map(String email){
        HashMap<String, String> response = new HashMap<>();
        response.put("email", email);
        return response;
    }
}
