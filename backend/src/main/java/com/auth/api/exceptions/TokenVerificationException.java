package com.auth.api.exceptions;

public class TokenVerificationException extends RuntimeException{
    public TokenVerificationException(String message){
        super(message);
    }
}
