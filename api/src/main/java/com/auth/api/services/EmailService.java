package com.auth.api.services;

import com.auth.api.entities.Email;
import com.auth.api.entities.User;

public interface EmailService {


    void sendActivationEmail(User user, String token);

    void sendResetPasswordEmail(User user, String token);

    void sendEmail(Email email);
}
