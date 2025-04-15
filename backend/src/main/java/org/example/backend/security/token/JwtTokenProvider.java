package org.example.backend.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.backend.entity.enums.Role;
import org.example.backend.exception.auth.ExpiredTokenException;
import org.example.backend.exception.auth.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long expirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException(); // 🔥 새 예외
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("지원하지 않는 JWT 형식입니다.");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("잘못된 JWT 구조입니다.");
        } catch (SignatureException e) {
            throw new InvalidTokenException("JWT 서명이 유효하지 않습니다.");
        } catch (Exception e) {
            throw new InvalidTokenException("JWT 처리 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
