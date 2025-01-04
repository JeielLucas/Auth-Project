package com.auth.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @Email (message = "Email deve ser válido")
        @NotBlank(message = "Email não pode ser vazia")
        String email,
        @NotBlank(message = "Senha não pode ser vazia")
        String password
){
}
