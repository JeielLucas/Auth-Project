package com.auth.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest (

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @NotBlank(message = "Senha não pode ser vazia")
        String password,

        @Size(min = 8, message = "A confirmação de senha deve ter no mínimo 8 caracteres")
        @NotBlank(message = "Confirmação de senha não pode ser vazia")
        String confirmPassword){}
