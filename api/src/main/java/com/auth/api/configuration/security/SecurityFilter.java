package com.auth.api.configuration.security;

import com.auth.api.exceptions.UnauthorizedException;
import com.auth.api.services.CookieServiceImpl;
import com.auth.api.services.JWTServiceImpl;
import com.auth.api.services.TokenServiceImpl;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;
    private final CookieServiceImpl cookieServiceImpl;
    private final JWTServiceImpl jwtServiceImpl;

    public SecurityFilter(TokenServiceImpl tokenServiceImpl, CookieServiceImpl cookieServiceImpl, JWTServiceImpl jwtServiceImpl) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.cookieServiceImpl = cookieServiceImpl;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = cookieServiceImpl.findCookieValue(request, "access_token");

        if(checkEndpointIsPublic(request)){
            filterChain.doFilter(request, response);
            return;
        }
        try{
            jwtServiceImpl.authenticateUserFromToken(token);
        }catch (JWTVerificationException e) {
            if (!tokenServiceImpl.generateAcessTokenByRefreshToken(request, response)) {
                log.warn("Acesso inválido");
                throw new UnauthorizedException("Acesso invalido");
            }
            filterChain.doFilter(request, response);
        }

    }

    private boolean checkEndpointIsPublic(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (method.equals("GET") && Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_GET).contains(requestURI)) {
            return true;
        } else if (method.equals("POST") && Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_POST).contains(requestURI)) {
            return true;
        } else return method.equals("PUT") && Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_PUT).contains(requestURI);
    }
}