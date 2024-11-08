package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.exceptions.EmailAlreadyExistsException;
import com.auth.api.exceptions.InvalidCredentialsException;
import com.auth.api.exceptions.MismatchException;
import com.auth.api.repositories.RegisterRequestDTO;
import com.auth.api.repositories.UserRepository;
import com.auth.api.repositories.LoginRequestDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
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

    public ResponseEntity requestPasswordReset(String email){
        User user = userRepository.findByEmail(email);

        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        String token = generateToken(user);

        emailService.sendResetPasswordEmail(user, token);

        return ResponseEntity.ok().body("Verifique seu email");
    }

    public ResponseEntity resetPassword(String token, String newPassword){
        String validationResponse = validateToken(token);

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

        String validationResponse =  validateToken(token);

        if(validationResponse.equals("Token expirado") || validationResponse.equals("Token inválido")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResponse);
        }

        return ResponseEntity.ok("Token válido");
    }

    private String generateToken(User user){
        try{
            Instant expiration = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));

            Algorithm algorithm = Algorithm.HMAC256("secret");

            String token = JWT.create()
                    .withIssuer("auth-project")
                    .withSubject(user.getEmail())
                    .withExpiresAt(Instant.from(expiration))
                    .sign(algorithm);
            return token;
        }catch (JWTVerificationException ex){
            return "";
        }
    }

    private String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256("secret");

            DecodedJWT decodedJWT =  JWT.require(algorithm)
                    .withIssuer("auth-project")
                    .build()
                    .verify(token);

            String email = decodedJWT.getSubject();
            Date expirationDate = decodedJWT.getExpiresAt();

            if(expirationDate != null && expirationDate.before(new Date())){
                return "Token expirado";
            }

            return email;
        }catch (Exception e){
            return "Token inválido";
        }
    }
}
