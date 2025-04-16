package org.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "token_blacklist")
public class TokenBlackList {

    @Id
    private String token;  // AccessToken 자체를 PK로 사용

    @Column(nullable = false)
    private LocalDateTime expiredAt;
}
