package com.auth.api.configs.security;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import com.auth.api.repositories.UserRepository;
import com.auth.api.services.TokenServiceImpl;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;
    private final UserRepository userRepository;

    public SecurityFilter(TokenServiceImpl tokenServiceImpl, UserRepository userRepository) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request, "access_token");
        if (token == null && !checkEndpointIsPublic(request)) {
            log.warn("Acesso inválido");
            sendUnauthorized(response, "Acesso inválido");
            return;
        }
        if(!checkEndpointIsPublic(request)) {
            try{
                authenticateUserFromToken(token);
            }catch (JWTVerificationException e){
                var isTokenGenerate = generateAcessTokenByRefreshToken(request, response);
                if(!isTokenGenerate){
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }

        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }

    private void authenticateUserFromToken(String token){

        String email = tokenServiceImpl.validateToken(token);

        User user = this.userRepository.findByEmail(email);
        if(user != null){
            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            var authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private boolean generateAcessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = this.recoverToken(request, "refresh_token");
        System.out.println("OIIII");
        if(refreshToken != null){
            try{
                String email = tokenServiceImpl.validateToken(refreshToken);
                User user = userRepository.findByEmail(email);
                if(user != null){
                    tokenServiceImpl.generateJWTandAddCookiesToResponse(user, response, "access_token", 30*60, false, true, 1);

                    log.warn("Enviando novo access_token");
                    authenticateUserFromToken(refreshToken);
                    return true;
                }else{
                    log.warn("User não encontrando");
                    sendUnauthorized(response, "Usuário não encontrado");
                    return false;
                }
            }catch (JWTVerificationException ex){
                log.warn("Refresh token invalido");
                sendUnauthorized(response, "Refresh token inválido ou expirado, faça o login novamente");
                return false;
            }
        }else{
            log.warn("Refresh token não encontrado");
            sendUnauthorized(response, "Refresh token não encontrado");
            return false;
        }

    }

    private boolean checkEndpointIsPublic(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (method.equals("GET") && Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_GET).contains(requestURI)) {
            return true;
        } else if (method.equals("POST") && Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_POST).contains(requestURI)) {
            return true;
        }
        return false;

    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(message);
    }
}