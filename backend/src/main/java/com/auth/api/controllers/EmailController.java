package com.auth.api.controllers;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.services.EmailServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/email")
public class EmailController {

    private final EmailServiceImpl emailServiceImpl;

    public EmailController(EmailServiceImpl emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponseDTO> getEmails(){
        return emailServiceImpl.getEmails();
    }
}
