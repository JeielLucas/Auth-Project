package com.auth.api.services;

import com.auth.api.entities.Email;
import com.auth.api.entities.User;
import com.auth.api.enums.StatusEmail;
import com.auth.api.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    private final String owner = "api-auth-project";
    private final String url = "http://localhost:8081";

    @Override
    @Async
    public void sendActivationEmail(User user, String token) {
        Email email = new Email();
        email.setOwnerRef(owner);
        email.setEmailTo(user.getEmail());
        email.setSubject("Ativação de conta - Auth Project");
        email.setText(
                "<p>Clique no link abaixo para ativar sua conta</p>" +
                "<a href='" + url + "/ativar-conta/" + token + "'>" + "Ativar conta</a>" +
                "<p>Esse token tem a validade de 30 minutos, após esse tempo precisa fazer o cadastro novamente.</p>"
        );
        email.setCreatedAt(LocalDateTime.now());

        sendEmail(email);
    }

    @Override
    @Async
    public void sendResetPasswordEmail(User user, String token) {
        Email email = new Email();
        email.setOwnerRef(owner);
        email.setEmailTo(user.getEmail());
        email.setSubject("Redefinição de senha - Auth Project");
        email.setText(
                "<p>Clique no link abaixo para redefinir sua senha</p>"+
                "<a href='" + url + "/redefinir-senha/" + token + "'>" + "Redefinir senha</a>" +
                "<p>Esse token tem a validade de 30 minutos, após esse tempo precisa solicitar a troca de senha novamente.</p>"
        );
        email.setCreatedAt(LocalDateTime.now());

        sendEmail(email);
    }

    @Override
    public void sendEmail(Email email){
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try{
            helper = new MimeMessageHelper(message, true);
            helper.setTo(email.getEmailTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getText(), true);

            mailSender.send(message);
            email.setStatusEmail(StatusEmail.SENT);
        }catch (MailException | MessagingException e){
            email.setStatusEmail(StatusEmail.ERROR);
        }finally {
            emailRepository.save(email);
        }
    }
}
