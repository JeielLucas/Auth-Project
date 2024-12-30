package com.auth.api.configuration.security;

import com.auth.api.exceptions.UnauthorizedException;
import com.auth.api.services.CookieServiceImpl;
import com.auth.api.services.JWTServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTServiceImpl jwtServiceImpl;
    private final CookieServiceImpl cookieServiceImpl;

    public SecurityFilter(JWTServiceImpl jwtServiceImpl, CookieServiceImpl cookieServiceImpl) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.cookieServiceImpl = cookieServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = cookieServiceImpl.findCookieValue(request, "access_token");
        if(token != null){
            try{
                jwtServiceImpl.validateAccessToken(request, response);
                filterChain.doFilter(request, response);
                return;
            }catch (UnauthorizedException ex){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(ex.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);


    }
}