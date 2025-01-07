package com.auth.api.handlers;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.dtos.ErrorResponseDTO;
import com.auth.api.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex){
        String errorMessage =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .findFirst()
                        .orElse("Erro desconhecido");

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), errorMessage, Collections.singletonList(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT.value(), "Email já cadastrado", Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDTO);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
    }

    @ExceptionHandler(MismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handlMismatchException(MismatchException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotActivatedException(AccountNotActivatedException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponseDTO);
    }

    @ExceptionHandler(TokenVerificationException.class)
    public ResponseEntity<ErrorResponseDTO> handleJWTVerificationException(TokenVerificationException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDTO);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTokenException(InvalidTokenException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(PasswordReuseException.class)
    public ResponseEntity<ErrorResponseDTO> handlePasswordReuseException(PasswordReuseException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponseDTO);
    }

    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<ApiResponseDTO> handleUserNotRegisteredException(UserNotRegisteredException ex){
        HashMap<String, String> response = new HashMap<>();
        response.put("email", ex.getEmail());

        ApiResponseDTO apiResponseDTO = new ApiResponseDTO(
                false,
                response,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiResponseDTO);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), Collections.singletonList(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
    }

}
