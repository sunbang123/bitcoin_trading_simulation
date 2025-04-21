package org.example.backend.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, String> {
    boolean existsByToken(String token);
}
