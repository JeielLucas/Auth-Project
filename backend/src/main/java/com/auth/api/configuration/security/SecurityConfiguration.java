package com.auth.api.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_POST = {
            "/api/v2/auth/login",
            "/api/v2/auth/register",
            "/api/v2/auth/forgot-password",
            "/api/v2/auth/login/google",
    };
    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_GET = {
            "/api/v2/auth/users/activate",
            "/api/v2/auth/users",
            "/api/v2/auth/check",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v2/auth/ping",
            "/api/v2/email/get"
    };
    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_PUT = {
            "/api/v2/auth/reset-password",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST, ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_GET).permitAll()
                        .requestMatchers(HttpMethod.PUT, ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED_PUT).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8081"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);

        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
