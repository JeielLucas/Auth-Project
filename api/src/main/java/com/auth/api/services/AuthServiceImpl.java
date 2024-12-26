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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenServiceImpl tokenServiceImpl;
    private final CookieServiceImpl cookieServiceImpl;
    private final JWTServiceImpl jwtServiceImpl;

    public AuthServiceImpl(UserRepository userRepository, EmailService emailService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenServiceImpl tokenServiceImpl, CookieServiceImpl cookieServiceImpl, JWTServiceImpl jwtServiceImpl) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenServiceImpl = tokenServiceImpl;
        this.cookieServiceImpl = cookieServiceImpl;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    public ResponseEntity<ApiResponse> register(RegisterRequestDTO userDTO) {

        validateEmails(userDTO);

        validatePasswords(userDTO.password(), userDTO.confirmPassword());

        ensureEmailNotRegistered(userDTO.email());

        String encryptedPassword = passwordEncoder.encode(userDTO.password());
        User user = new User(userDTO.email(), encryptedPassword, UserRole.USER);
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());

        tokenServiceImpl.generateUUIDToken("activation", user);

        userRepository.save(user);

        emailService.sendActivationEmail(user, user.getToken());

        log.info("Usuário cadastrado com suceso, enviando email de ativação para {}", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "", "Usuário criado com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponse> login(LoginRequestDTO userDTO, HttpServletResponse response) {

        User user = userExists(userDTO.email());

        inactivedUser(user);

        authenticateUser(userDTO);

        issueJwtCookies(user, response);
        log.info("Usuário {} autenticado com sucesso, as {}", user.getEmail(), new Date());

        return ResponseEntity.ok(new ApiResponse<>(true, "", "Login efetuado com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponse> activateUser(String token, HttpServletResponse response) {

        User user = tokenServiceImpl.validateAndGetUserByToken("activation", token);

        if(user.isActive()){
            log.warn("Usuário {} já está ativo", user.getEmail());
            throw new InvalidOperationException("Usuário já ativo");
        }

        user.setActive(true);
        user.setToken(null);
        user.setTokenType(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        issueJwtCookies(user, response);

        log.info("Usuário {} ativado com sucesso", user.getEmail());

        return ResponseEntity.ok().body(new ApiResponse(true, "", "Conta ativada com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponse> requestPasswordReset(String email){
        User user = userExists(email);

        inactivedUser(user);

        tokenServiceImpl.generateUUIDToken("reset-password", user);

        emailService.sendResetPasswordEmail(user, user.getToken());

        userRepository.save(user);

        log.info("Reset de senha para o usuário {} enviado com sucesso", user.getEmail());

        return ResponseEntity.ok().body(new ApiResponse(true, "", "Verifique seu email"));
    }

    @Override
    public ResponseEntity<ApiResponse> resetPassword(String token, ResetPasswordRequest passwordRequest){
        User user = tokenServiceImpl.validateAndGetUserByToken("reset-password", token);

        validatePasswords(passwordRequest.password(), passwordRequest.confirmPassword());


        if(passwordEncoder.matches(passwordRequest.password(), user.getPassword()) ){
            log.warn("A nova senha não pode ser igual a antiga");
            throw new PasswordReuseException("A nova senha não pode ser igual a antiga");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.password()));

        user.setToken(null);
        user.setTokenType(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        log.info("Reset de senha com sucesso para o usuário {}", user.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "", "Senha redefinida com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponse> validateToken(HttpServletRequest request, HttpServletResponse response){
        String token = cookieServiceImpl.findCookieValue(request, "access_token");
        try{
            String validationResponse =  jwtServiceImpl.validateToken(token);
            log.info("Token {} validado com sucesso", token);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, validationResponse, "Token validado com sucesso"));
        }catch (JWTVerificationException ex){
            var isTokenGenerate = tokenServiceImpl.generateAcessTokenByRefreshToken(request, response);
            if (!isTokenGenerate) {
                log.warn("A verificação no token {} falhou por conta de {}", token, ex.getMessage());
                throw new TokenVerificationException(ex.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Refresh token validado com sucesso"));
        }
    }

    @Override
    public ResponseEntity<ApiResponse> loginWithGoogle(String token, HttpServletResponse httpResponse){
        String email = tokenServiceImpl.extractGoogleEmail(token);

        User user = userRepository.findByEmail(email);

        if(user == null){
            log.info("Usuário {} precisa cadastrar antes de fazer o login", email);
            HashMap<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(false, response, "Usuario precisa se cadastrar"));
        }

        inactivedUser(user);

        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, httpResponse, "access_token",30*60, false, true, 1);

        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, httpResponse, "refresh_token", 3*24*60*60, false, true, 72);

        log.info("Usuário {} autenticado com sucesso, as {}", user.getEmail(), new Date());

        return ResponseEntity.ok(new ApiResponse<>(true, user.getEmail(), "Login efetuado com sucesso"));
    };

    private void inactivedUser(User user){
        if(!user.isActive()){
            log.warn("Usuário {} inativo, necessita confirmar via email", user.getEmail());
            throw new AccountNotActivatedException("Usuário inativo, por favor, verifique seu email");
        }
    }

    private void validateEmails(RegisterRequestDTO userDTO){
        if(!userDTO.email().equals(userDTO.confirmEmail())){
            log.warn("Emails não coincidem {}, {}", userDTO.email(), userDTO.confirmEmail());
            throw new MismatchException("Emails não coincidem");
        }
    }

    private void validatePasswords(String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            log.warn("Senhas não coincidem {}, {}", password, confirmPassword);
            throw new MismatchException("Senhas não coincidem");
        }
    }

    private void ensureEmailNotRegistered(String email){
        if(userRepository.findByEmail(email) != null) {
            log.warn("Email já cadastrado {}", email);
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }
    }

    private void authenticateUser(LoginRequestDTO userDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.email(), userDTO.password()));
        }catch(Exception e){
            System.out.println(e + " | " + e.getMessage());
        }
    }

    private void issueJwtCookies(User user, HttpServletResponse response){
        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "access_token",20, false, true, 1);

        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "refresh_token", 3*24*60*60, false, true, 72);
    }

    private User userExists(String email){
        User user = userRepository.findByEmail(email);

        if(user == null){
            log.warn("O email {} não está cadastrado", email);
            throw new InvalidCredentialsException("Email não cadastrado");
        }

        return user;
    }
}
