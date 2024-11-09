package com.auth.api.handlers;

import com.auth.api.dtos.ErrorResponse;
import com.auth.api.exceptions.EmailAlreadyExistsException;
import com.auth.api.exceptions.InvalidCredentialsException;
import com.auth.api.exceptions.MismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex){
        List<String> erros = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erros.add(error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Erro de validação nos campos fornecidos", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "Email já existe", Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Credenciais inválidas", Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MismatchException.class)
    public ResponseEntity<ErrorResponse> handlMismatchException(MismatchException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Erro de correspondência", Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
