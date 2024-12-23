package com.auth.api.services;

import com.auth.api.entities.Email;
import com.auth.api.entities.User;
import com.auth.api.enums.StatusEmail;
import com.auth.api.repositories.EmailRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {


    private final EmailRepository emailRepository;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(EmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendActivationEmail(User user, String token) {
        Email email = new Email();
        email.setOwnerRef("api-auth-project");
        email.setEmailTo(user.getEmail());
        email.setSubject("Ativação de conta - Auth Project");
        email.setText(
                "Clique no link abaixo para ativar sua conta \n" +
                "http://localhost:5173/ativar-conta/" + token
        );
        email.setCreatedAt(LocalDateTime.now());

        sendEmail(email);
    }

    @Override
    @Async
    public void sendResetPasswordEmail(User user, String token) {
        Email email = new Email();
        email.setOwnerRef("api-auth-project");
        email.setEmailTo(user.getEmail());
        email.setSubject("Redefinição de senha - Auth Project");
        email.setText(
                "Clique no link abaixo para redefinir sua senha \n"
                + "http://localhost:5173/redefinir-senha/" + token
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
