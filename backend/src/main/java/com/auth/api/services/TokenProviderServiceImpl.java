package com.auth.api.services;

import com.auth.api.entities.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class TokenProviderServiceImpl implements TokenProvider{

    private final org.springframework.core.env.Environment environment;

    public TokenProviderServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String generateToken(UserDetailsImpl user, int durationInHours) {
        String secretKey = environment.getProperty("JWT_SECRET_KEY");
        String issuer = environment.getProperty("JWT_ISSUER");
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getUsername())
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate(durationInHours))
                    .sign(algorithm);
        }catch (JWTCreationException ex){
            log.error("Erro ao gerar o token: " + ex.getMessage());
            throw new JWTCreationException("Erro ao gerar o token: ", ex);
        }
    }

    @Override
    public String validateToken(String token) {
        String secretKey = environment.getProperty("JWT_SECRET_KEY");
        String issuer = environment.getProperty("JWT_ISSUER");
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException ex){
            log.error("Erro ao verificar o token: " + ex.getMessage());
            throw new JWTVerificationException("Erro ao verificar o token: " + ex.getMessage(), ex);
        }
    }

    private Instant creationDate(){
        return Instant.now();
    }

    private Instant expirationDate(int durationInHours){
        return Instant.now().plus(durationInHours, ChronoUnit.HOURS);
    }
}
