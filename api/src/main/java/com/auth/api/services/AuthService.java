package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.enums.UserRole;
import com.auth.api.exceptions.EmailAlreadyExistsException;
import com.auth.api.exceptions.MismatchException;
import com.auth.api.repositories.RegisterRequestDTO;
import com.auth.api.repositories.UserRepository;
import com.auth.api.repositories.LoginRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TokenService tokenService;


    public AuthService(UserRepository userRepository, EmailService emailService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;

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


        User user = new User(userDTO.email(), userDTO.password(), UserRole.USER);

        user.setActive(false);

        String token = UUID.randomUUID().toString();

        user.setActivationToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        emailService.sendActivationEmail(user, token);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registado com sucesso");
    }

    public ResponseEntity<String> LoginUser(LoginRequestDTO userDTO){

        User user = userRepository.findByEmail(userDTO.email());

        if(user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não encontrado");
        }

        if(!user.isActive()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário inativo, por favor, verifique seu email");
        }

        String token = tokenService.generateToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(token);
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

    public ResponseEntity requestPasswordReset(String email){
        User user = userRepository.findByEmail(email);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        String token = tokenService.generateToken(user);

        emailService.sendResetPasswordEmail(user, token);

        return ResponseEntity.ok().body("Verifique seu email");
    }

    public ResponseEntity resetPassword(String token, String newPassword){
        String validationResponse = tokenService.validateToken(token);

        if(validationResponse.equals("Token inválido") || validationResponse.equals("Token expirado")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResponse);
        }

        User user = userRepository.findByEmail(validationResponse);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        user.setPassword(newPassword);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso");
    }

    public ResponseEntity validarToken(String token){

        String validationResponse =  tokenService.validateToken(token);

        if(validationResponse.equals("Token expirado") || validationResponse.equals("Token inválido")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResponse);
        }

        return ResponseEntity.ok("Token válido");
    }
}
