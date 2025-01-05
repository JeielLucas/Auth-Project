package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.exceptions.TokenVerificationException;
import com.auth.api.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService{


    private final UserRepository userRepository;

    public TokenServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

            return decodedJWT.getClaim("email").asString();
        }catch (JWTDecodeException ex){
            log.error("Token invalido");
            throw new TokenVerificationException(ex.getMessage());
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
