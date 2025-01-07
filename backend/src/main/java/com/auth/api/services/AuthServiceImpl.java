package com.auth.api.services;

import com.auth.api.dtos.*;
import com.auth.api.entities.User;
import com.auth.api.enums.UserRole;
import com.auth.api.exceptions.*;
import com.auth.api.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public ResponseEntity<ApiResponseDTO> register(RegisterRequestDTO userDTO, HttpServletResponse response) {
        cookieServiceImpl.clearCookies(response);

        validateEmails(userDTO);
        validatePasswords(userDTO.password(), userDTO.confirmPassword());

        User user = ensureEmailNotRegistered(userDTO.email());

        String encryptedPassword = passwordEncoder.encode(userDTO.password());

        if(user == null){
            user = new User(userDTO.email(), UserRole.USER);
            user.setActive(false);
            user.setCreatedAt(LocalDateTime.now());
        }

        if(user.getToken() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())){
            tokenServiceImpl.generateUUIDToken("activation", user);
        }

        user.setPassword(encryptedPassword);

        userRepository.save(user);

        emailService.sendActivationEmail(user, user.getToken());

        log.info("Usuário cadastrado com suceso, enviando email de ativação para {}", user.getEmail());

        UserResponseDTO userResponseDTO = new UserResponseDTO(user.getEmail(), user.getRole(), user.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>(true, userResponseDTO, "Usuário criado com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> login(LoginRequestDTO userDTO, HttpServletResponse response) {
        cookieServiceImpl.clearCookies(response);

        User user = userExists(userDTO.email());

        inactivedUser(user);

        authenticateUser(userDTO);

        issueJwtCookies(user, response);

        log.info("Usuário {} autenticado com sucesso, as {}", user.getEmail(), new Date());

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "", "Login efetuado com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> activateUser(String token, HttpServletResponse response) {

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

        return ResponseEntity.ok().body(new ApiResponseDTO(true, "", "Conta ativada com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> requestPasswordReset(String email){
        User user = userExists(email);

        inactivedUser(user);

        tokenServiceImpl.generateUUIDToken("reset-password", user);

        emailService.sendResetPasswordEmail(user, user.getToken());

        userRepository.save(user);

        log.info("Reset de senha para o usuário {} enviado com sucesso", user.getEmail());

        return ResponseEntity.ok().body(new ApiResponseDTO(true, "", "Verifique seu email"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> resetPassword(String token, ResetPasswordRequest passwordRequest){
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

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, "", "Senha redefinida com sucesso"));
    }

    @Override
    public ResponseEntity<ApiResponseDTO> loginWithGoogle(String token, HttpServletResponse httpResponse){
        String email = tokenServiceImpl.extractGoogleEmail(token);

        User user = userRepository.findByEmail(email);

        if(user == null){
            log.info("Usuário {} precisa cadastrar antes de fazer o login", email);
            HashMap<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponseDTO<>(false, response, "Usuario precisa se cadastrar"));
        }

        inactivedUser(user);

        issueJwtCookies(user, httpResponse);

        log.info("Usuário {} autenticado com sucesso, as {}", user.getEmail(), new Date());

        return ResponseEntity.ok(new ApiResponseDTO<>(true, user.getEmail(), "Login efetuado com sucesso"));
    };

    @Override
    public ResponseEntity<ApiResponseDTO> logout(HttpServletResponse response){
        return cookieServiceImpl.clearCookies(response);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> checkAuth(HttpServletRequest request, HttpServletResponse response){
        try{
            return jwtServiceImpl.validateAccessToken(request, response);
        }catch (UnauthorizedException ex){
            throw new UnauthorizedException("Refresh token nao encontrado");
        }
    }

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

    private User ensureEmailNotRegistered(String email){
        User user = userRepository.findByEmail(email);

        if(user != null){
            if("activation".equals(user.getTokenType())){
                if(user.getTokenExpiration().isAfter(LocalDateTime.now())){
                    throw new AccountNotActivatedException("Usuário cadastrado, necessita de ativação, verifique seu email");
                }else{
                    tokenServiceImpl.generateUUIDToken("activation", user);
                    return user;
                }
            }
            log.warn("Email já cadastrado {}", email);
            throw new EmailAlreadyExistsException("Email " + email + " já está cadastrado");
        }

        return null;
    }

    private void authenticateUser(LoginRequestDTO userDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.email(), userDTO.password()));
        }catch(BadCredentialsException e){
            log.warn("Erro ao autenticar " + e.getMessage());
            throw new InvalidCredentialsException("Credenciais invalidas");
        }
    }

    private void issueJwtCookies(User user, HttpServletResponse response){
        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "access_token", 30*60, false, true, 30);

        cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "refresh_token", 3*24*60*60, false, true, 72*60);

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
