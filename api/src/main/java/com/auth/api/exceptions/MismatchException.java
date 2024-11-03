package com.auth.api.exceptions;

public class MismatchException extends RuntimeException{
    public MismatchException(String message){
        super(message);
    }
}
