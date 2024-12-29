package com.auth.api.services;

import com.auth.api.dtos.ApiResponseDTO;
import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import com.auth.api.exceptions.TokenVerificationException;
import com.auth.api.exceptions.UnauthorizedException;
import com.auth.api.repositories.UserRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTServiceImpl implements JWTService {

    private final UserRepository userRepository;
    private final CookieServiceImpl cookieServiceImpl;
    private final TokenProviderServiceImpl tokenProviderServiceImpl;

    public JWTServiceImpl(UserRepository userRepository, CookieServiceImpl cookieServiceImpl, TokenProviderServiceImpl tokenProviderServiceImpl) {
        this.userRepository = userRepository;
        this.cookieServiceImpl = cookieServiceImpl;
        this.tokenProviderServiceImpl = tokenProviderServiceImpl;
    }

    @Override
    public void authenticateUserFromToken(String token){
        String email = tokenProviderServiceImpl.validateToken(token);

        User user = this.userRepository.findByEmail(email);
        if(user != null){
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Override
    public ResponseEntity<ApiResponseDTO> validateAccessToken(HttpServletRequest request, HttpServletResponse response){
        String token = cookieServiceImpl.findCookieValue(request, "access_token");
        try{
            String validationResponse = tokenProviderServiceImpl.validateToken(token);
            log.info("Token {} validado com sucesso", token);
            authenticateUserFromToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, validationResponse, "Token validado com sucesso"));
        }catch (JWTVerificationException ex){
            var isTokenGenerate = generateAccessTokenByRefreshToken(request, response);
            if (!isTokenGenerate) {
                log.warn("A verificação no token {} falhou por conta de {}", token, ex.getMessage());
                throw new TokenVerificationException(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, "Refresh token validado com sucesso"));
        }
    }

    private boolean generateAccessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieServiceImpl.findCookieValue(request, "refresh_token");
        if (refreshToken != null){
            try {
                String email = tokenProviderServiceImpl.validateToken(refreshToken);
                User user = userRepository.findByEmail(email);

                if(user != null){
                    cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "access_token", 30 * 60, false, true, 1);
                    log.warn("Enviando novo access_token");
                    authenticateUserFromToken(refreshToken);
                    return true;
                }else{
                    log.error("Usuario {} não encontrado", email);
                    throw new UnauthorizedException("Usuario não encontrado");
                }
            }catch(JWTVerificationException ex){
                log.error("Refresh token invalido");
                throw new UnauthorizedException("Refresh token invalido ou expirado, faca o login novamente");
            }
        }else{
            log.error("Refresh token nao encontrado");
            throw new UnauthorizedException("Refresh token nao encontrado");
        }
    }
}
