package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import com.auth.api.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class JWTServiceImpl implements JWTService {

    private final String SECRET_KEY = "secret";
    private final String ISSUER = "auth-api";

    private final UserRepository userRepository;

    public JWTServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
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
            log.error("Erro ao gerar o token: ", ex.getMessage());
            throw new JWTCreationException("Erro ao gerar o token: ", ex);
        }
    }

    @Override
    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException ex){
            log.error("Erro ao verificar o token: ", ex.getMessage());
            throw new JWTVerificationException("Erro ao verificar o token: " + ex.getMessage(), ex);
        }
    }

    public void authenticateUserFromToken(String token){

        String email = validateToken(token);
        User user = this.userRepository.findByEmail(email);
        if(user != null){
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private Instant creationDate(){
        return Instant.now();
    }

    private Instant expirationDate(int durationInHours){
        return Instant.now().plus(durationInHours, ChronoUnit.HOURS);
    }
}
