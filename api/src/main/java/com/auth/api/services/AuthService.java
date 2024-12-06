package com.auth.api.services;

import com.auth.api.dtos.ApiResponse;
import com.auth.api.dtos.LoginRequestDTO;
import com.auth.api.dtos.RegisterRequestDTO;
import com.auth.api.dtos.ResetPasswordRequest;
import com.auth.api.entities.User;
import com.auth.api.enums.UserRole;
import com.auth.api.exceptions.*;
import com.auth.api.repositories.UserRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthService(UserRepository userRepository, EmailService emailService, TokenService tokenService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<ApiResponse> RegisterUser(RegisterRequestDTO userDTO) {

        if(!userDTO.email().equals(userDTO.confirmEmail())){
            log.warn("Emails não coincidem {}, {}", userDTO.email(), userDTO.confirmEmail());
            throw new MismatchException("Emails não coincidem");
        }

        if(!userDTO.password().equals(userDTO.confirmPassword())){
            log.warn("Senhas não coincidem {}, {}", userDTO.password(), userDTO.confirmPassword());
            throw new MismatchException("Senhas não coincidem");
        }

        if(userRepository.findByEmail(userDTO.email()) != null) {
            log.warn("Email já cadastrado {}", userDTO.email());
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        String encryptedPassword = passwordEncoder.encode(userDTO.password());

        User user = new User(userDTO.email(), encryptedPassword, UserRole.USER);

        user.setActive(false);

        user = generateToken("activation", user);

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        emailService.sendActivationEmail(user, user.getToken());

        log.info("Usuário cadastrado com suceso, enviando email de ativação para {}", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "", "Usuário criado com sucesso"));
    }

    public ResponseEntity<ApiResponse> LoginUser(LoginRequestDTO userDTO, HttpServletResponse response) {

        User user = userRepository.findByEmail(userDTO.email());

        if(user == null) {
            log.warn("Email não cadastrado {}", userDTO.email());
            throw new InvalidCredentialsException("Email não cadastrado");
        }

        if(!user.isActive()){
            log.warn("Usuário {} inativo, necessita confirmar via email", user.getEmail());
            throw new AccountNotActivatedException("Usuário inativo, por favor, verifique seu email");
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(userDTO.email(), userDTO.password());

        try{
            Authentication authentication = authenticationManager.authenticate(usernamePassword);

            tokenService.generateJWTandAddCookiesToResponse(user, response, "access_token",60*60, false, true, 1);

            tokenService.generateJWTandAddCookiesToResponse(user, response, "refresh_token", 3*24*60*60, false, true, 72);

            log.info("Usuário {} autenticado com sucesso, as {}", user.getEmail(), new Date());

            return ResponseEntity.ok(new ApiResponse<>(true, "", "Sucess"));
        }catch (AuthenticationException e){
            log.warn(e.getMessage());
            throw new InvalidCredentialsException("Senha incorreta");
        }
    }

    public ResponseEntity<ApiResponse> activateUser(String token, HttpServletResponse response) { //Tratar erro de usuário nulo
        User user = userRepository.findByToken(token);

        if(user == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            try{
                log.warn("Token de ativação inválido ou expirou as {}", user.getTokenExpiration());
            }catch(NullPointerException e){
                log.warn("Token de ativação inválido");
            }
            throw new InvalidTokenException("Token inválido ou expirado");
        }

        if(user.isActive()){
            log.warn("Usuário {} já está ativo", user.getEmail());
            throw new TokenVerificationException("Usuário já ativo");
        }

        if(!user.getTokenType().equals("activation")){
            log.warn("O token não é do tipo activation");
            throw new TokenVerificationException("Token do tipo incorreto");
        }

        user.setActive(true);
        user.setToken(null);
        user.setTokenType(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        tokenService.generateJWTandAddCookiesToResponse(user, response, "access_token", 60*60, false, true, 1);
        tokenService.generateJWTandAddCookiesToResponse(user, response, "refresh_token", 3*24*60*60, false, true, 72);

        log.info("Usuário {} ativado com sucesso", user.getEmail());

        return ResponseEntity.ok().body(new ApiResponse(true, "", "Conta ativada com sucesso"));
    }

    public ResponseEntity<ApiResponse> requestPasswordReset(String email){
        User user = userRepository.findByEmail(email);

        if(user == null){
            log.warn("O email {} não está cadastrado", email);
            throw new InvalidCredentialsException("Email não cadastrado");
        }

        if(!user.isActive()){
            log.warn("O usuário {} não está ativo", email);
            throw new AccountNotActivatedException("Usuário inativo, por favor, verifique seu email");
        }

        user = generateToken("reset-password", user);

        emailService.sendResetPasswordEmail(user, user.getToken());

        userRepository.save(user);

        log.info("Reset de senha para o usuário {} enviado com sucesso", user.getEmail());

        return ResponseEntity.ok().body(new ApiResponse(true, "", "Verifique seu email"));
    }

    public ResponseEntity<ApiResponse> resetPassword(String token, ResetPasswordRequest passwordRequest){
        try{

            User user = userRepository.findByToken(token);

            if(user == null){
                log.warn("O token {} é inválido", token);
                throw new InvalidTokenException("Token inválido");
            }

            if(user.getTokenExpiration().isBefore(LocalDateTime.now())){
                log.warn("Token de redefinir senha inválido ou expirou as {}", user.getTokenExpiration());
                throw new TokenVerificationException("Token expirado");
            }

            if(!user.getTokenType().equals("reset-password")){
                log.warn("O token não é do tipo reset-password");
                throw new TokenVerificationException("Token do tipo incorreto");
            }

            if(!passwordRequest.password().equals(passwordRequest.confirmPassword())){
                log.warn("As senhas não coincidem");
                throw new MismatchException("Senhas não coincidem");
            }

            user.setPassword(passwordEncoder.encode(passwordRequest.password()));

            user.setToken(null);
            user.setTokenType(null);
            user.setTokenExpiration(null);

            userRepository.save(user);

            log.info("Reset de senha com sucesso para o usuário {}", user.getEmail());

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "", "Senha redefinida com sucesso"));

        }catch (JWTVerificationException ex){
            throw new JWTVerificationException(ex.getMessage());
        }
    }

    public ResponseEntity<ApiResponse> validarToken(HttpServletRequest request){
        String token = extractToken(request);
        try{
            String validationResponse =  tokenService.validateToken(token);
            log.info("Token {} validado com sucesso", token);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "", validationResponse));
        }catch (JWTVerificationException ex){
            log.warn("A verificação no token {} falhou por conta de {}", token, ex.getMessage());
            throw new TokenVerificationException(ex.getMessage());
        }
    }

    private String extractToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("access_token")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private User generateToken(String type, User user){
        String token = UUID.randomUUID().toString();

        user.setToken(token);
        user.setTokenType(type);
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));

        return user;
    }
}
