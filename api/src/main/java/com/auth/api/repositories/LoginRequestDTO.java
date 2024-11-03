package com.auth.api.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @Email (message = "Email deve ser válido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @NotBlank(message = "Senha não pode ser vazio")
        String password
){
}
