package com.auth.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Email (message = "Email deve ser válido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,
        @NotBlank(message = "Senha não pode ser vazia")
        String password
){
}
