package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.exceptions.EmailAlreadyExistsException;
import com.auth.api.exceptions.InvalidCredentialsException;
import com.auth.api.exceptions.MismatchException;
import com.auth.api.repositories.RegisterRequestDTO;
import com.auth.api.repositories.UserRepository;
import com.auth.api.repositories.LoginRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public ResponseEntity<String> RegisterUser(RegisterRequestDTO userDTO) {

        if(!userDTO.email().equals(userDTO.confirmEmail())){
            throw new MismatchException("Emails não coincidem");
        }

        if(!userDTO.password().equals(userDTO.confirmPassword())){
            throw new MismatchException("Senhas não coincidem");
        }

        if(userRepository.findByEmail(userDTO.email()) != null) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }


        User user = new User(userDTO.email(), userDTO.password());

        user.setActive(false);

        String token = UUID.randomUUID().toString();

        user.setActivationToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        emailService.sendActivationEmail(user, token);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registado com sucesso");
    }

    public ResponseEntity<String> LoginUser(LoginRequestDTO user){
        User newUser = userRepository.findByEmail(user.email());

        if(newUser == null) {
            throw new InvalidCredentialsException("Email não cadastrado");
        }

        if(!user.password().equals(newUser.getPassword())) {
            throw new InvalidCredentialsException("Senha incorreta");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Usuário logado com sucesso");
    }

    public ResponseEntity activateUser(String token){

        User user = userRepository.findByActivationToken(token);

        if(user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado");
        }

        user.setActive(true);
        user.setActivationToken(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        return ResponseEntity.ok().body("Conta ativada com sucesso");
    }
}
