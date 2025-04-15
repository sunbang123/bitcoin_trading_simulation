package org.example.backend.security.config;

public class AuthWhitelist {
    public static final String[] NO_AUTH_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/**",
            "/api/users/**"
    };
}