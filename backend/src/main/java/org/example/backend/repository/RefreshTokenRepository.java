package org.example.backend.repository;

import org.example.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByJtiAndRevokedFalse(String jti);
    void deleteByJti(String jti);
}
