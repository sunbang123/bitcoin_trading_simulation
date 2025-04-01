package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.entity.enums.Side;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Fill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String market; // 예: KRW-BTC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side; // BUY / SELL

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double volume;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Fill of(String market, Side side, double price, double volume, User user) {
        return Fill.builder()
                .market(market)
                .side(side)
                .price(price)
                .volume(volume)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }
}
