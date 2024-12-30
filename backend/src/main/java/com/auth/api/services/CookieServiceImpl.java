package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class CookieServiceImpl implements CookieService {

    private final TokenProviderServiceImpl tokenProviderServiceImpl;

    public CookieServiceImpl(TokenProviderServiceImpl tokenProviderServiceImpl) {
        this.tokenProviderServiceImpl = tokenProviderServiceImpl;
    }

    @Override
    public String findCookieValue(HttpServletRequest request, String name){
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

    @Override
    public void generateJWTandAddCookiesToResponse(User user, HttpServletResponse response, String name, int maxAge, boolean secure, boolean httpOnly, int duration){
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String token = tokenProviderServiceImpl.generateToken(userDetails, duration);

        Cookie cookie = new Cookie(name, token);

        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
