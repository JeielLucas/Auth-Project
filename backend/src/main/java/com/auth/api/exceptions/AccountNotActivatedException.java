package com.auth.api.exceptions;

public class AccountNotActivatedException extends RuntimeException{
    public AccountNotActivatedException(String message){
        super(message);
    }
}
