package com.auth.api.services;

import com.auth.api.entities.User;
import com.auth.api.entities.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {


    private final JWTServiceImpl jwtServiceImpl;

    public CookieServiceImpl(JWTServiceImpl jwtServiceImpl){
        this.jwtServiceImpl = jwtServiceImpl;
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

        String token = jwtServiceImpl.generateToken(userDetails, duration);

        Cookie cookie = new Cookie(name, token);

        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
