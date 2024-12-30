package com.auth.api.services;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.entities.Email;
import com.auth.api.entities.User;
import org.springframework.http.ResponseEntity;

public interface EmailService {


    void sendActivationEmail(User user, String token);

    void sendResetPasswordEmail(User user, String token);

    void sendEmail(Email email);

    ResponseEntity<ApiResponseDTO> getEmails();
}
