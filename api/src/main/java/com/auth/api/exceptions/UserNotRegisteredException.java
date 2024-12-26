package com.auth.api.exceptions;

public class UserNotRegisteredException extends RuntimeException {

    private final String email;

    public UserNotRegisteredException(String email) {
        super("Usuário precisa cadastrar antes de fazer o login");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
