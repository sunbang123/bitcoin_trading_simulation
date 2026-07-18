package org.example.backend.global.security.jwt;

public class AuthWhitelist {
    public static final String[] NO_AUTH_PATHS = {
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/rankings",
            "/api/markets/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };
}
