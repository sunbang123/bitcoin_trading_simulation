package org.example.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.token.TokenBlackList;
import org.example.backend.auth.token.TokenBlackListRepository;
import org.example.backend.common.security.token.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenBlackListRepository tokenBlackListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void logout(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            LocalDateTime expiredAt = jwtTokenProvider.getExpiration(token);

            tokenBlackListRepository.save(TokenBlackList.builder()
                    .token(token)
                    .expiredAt(expiredAt)
                    .build());
        }
    }
}
