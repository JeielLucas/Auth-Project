package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final String SECRET_KEY = "secret";
    private final String ISSUER = "auth-api";

    public String generateToken(UserDetailsImpl user, int durationInHours){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getUsername())
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate(durationInHours))
                    .sign(algorithm);
        }catch (JWTCreationException ex){
            throw new JWTCreationException("Erro ao gerar o token: ", ex);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException ex){
            throw new JWTVerificationException("Erro ao verificar o token: " + ex.getMessage(), ex);
        }
    }

    public void generateJWTandAddCookiesToResponse(User user, HttpServletResponse response, String name, int maxAge, boolean secure, boolean httpOnly, int duration){
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String token = generateToken(userDetails, duration);

        Cookie cookie = new Cookie(name, token);

        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private Instant creationDate(){
        return Instant.now();
    }

    private Instant expirationDate(int durationInHours){
        return Instant.now().plus(durationInHours, ChronoUnit.HOURS);
    }
}
