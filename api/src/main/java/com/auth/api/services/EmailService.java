package com.auth.api.services;

import com.auth.api.entities.Email;
import com.auth.api.entities.User;
import com.auth.api.enums.StatusEmail;
import com.auth.api.repositories.EmailRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {


    private final EmailRepository emailRepository;
    private final JavaMailSender mailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(User user, String token) {
        Email email = new Email();
        email.setOwnerRef("api-auth-project");
        email.setEmailTo(user.getEmail());
        email.setSubject("Ativação de conta - Auth Project");
        email.setText(
                "Clique no link abaixo para ativar sua conta \n" +
                "http://localhost:8080/api/v1/auth/ativar-conta?token=" + token
        );
        email.setCreatedAt(LocalDateTime.now());

        sendEmail(email);
    }



    private void sendEmail(Email email){
        SimpleMailMessage message = new SimpleMailMessage();

        try{
            message.setTo(email.getEmailTo());
            message.setSubject(email.getSubject());
            message.setText(email.getText());

            mailSender.send(message);

            email.setStatusEmail(StatusEmail.SENT);
        }catch (MailException e){
            email.setStatusEmail(StatusEmail.ERROR);
        }finally {
            emailRepository.save(email);
        }
    }
}
