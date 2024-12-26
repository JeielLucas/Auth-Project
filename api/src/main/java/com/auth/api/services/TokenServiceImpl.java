package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.exceptions.TokenVerificationException;
import com.auth.api.exceptions.UnauthorizedException;
import com.auth.api.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService{


    private final UserRepository userRepository;
    private final JWTServiceImpl jwtServiceImpl;
    private final CookieServiceImpl cookieServiceImpl;

    public TokenServiceImpl(UserRepository userRepository, JWTServiceImpl jwtServiceImpl, CookieServiceImpl cookieServiceImpl) {
        this.userRepository = userRepository;
        this.jwtServiceImpl = jwtServiceImpl;
        this.cookieServiceImpl = cookieServiceImpl;
    }

    @Override
    public User generateUUIDToken(String type, User user) {
        String token = UUID.randomUUID().toString();

        user.setToken(token);
        user.setTokenType(type);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(30));

        return user;
    }

    @Override
    public String extractGoogleEmail(String token){
        try{
            DecodedJWT decodedJWT = JWT.decode(token);

            String email = decodedJWT.getClaim("email").asString();

            return email;
        }catch (JWTDecodeException ex){
            log.error("Token invalido");
            throw new TokenVerificationException(ex.getMessage());
        }
    }

    @Override
    public boolean generateAcessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = cookieServiceImpl.findCookieValue(request, "refresh_token");
        if(refreshToken != null){
            try{
                String email = jwtServiceImpl.validateToken(refreshToken);
                User user = userRepository.findByEmail(email);

                if(user != null){
                    cookieServiceImpl.generateJWTandAddCookiesToResponse(user, response, "access_token", 30*60, false, true, 1);

                    log.warn("Enviando novo access_token");
                    jwtServiceImpl.authenticateUserFromToken(refreshToken);
                    return true;
                }else{
                    log.error("Usuario {} não encontrado", email);
                    throw new UnauthorizedException("Usuario não encontrado");
                }
            }catch (JWTVerificationException ex){
                log.error("Refresh token invalido");
                throw new UnauthorizedException("Refresh token invalido ou expirado, faça o login novamente");
            }
        }else{
            log.error("Refresh token nao encontrado");
            throw new UnauthorizedException("Refresh token nao encontrado");
        }
    }

    @Override
    public User validateAndGetUserByToken(String type, String token){
        User user = userRepository.findByToken(token);

        if(user == null){
            log.error("O token {} é inválido", token);
            throw new TokenVerificationException("Token invalido");
        }

        if(user.getTokenExpiration().isBefore(LocalDateTime.now())){
            log.error("Token de {} invalido ou expirou as {}", type, user.getTokenExpiration());
            throw new TokenVerificationException("Token expirado");
        }

        if(!user.getTokenType().equals(type)){
            log.error("O token nao é do tipo {}", type);
            throw new TokenVerificationException("Token do tipo incorreto");
        }

        return user;
    }

}
