package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "account")
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 화폐를 의미하는 영문 대문자 코드 ex. BTC, ETH
    @Column(nullable = false)
    private String currency;

    // 주문가능 금액/수량
    @Column(nullable = false)
    private double balance;

    // 주문 중 묶여있는 금액/수량
    @Column(nullable = false)
    private double locked;

    // 매수평균가
    @Column(nullable = false)
    private double avgBuyPrice;

    // 매수평균가 수정 여부
    @Column(nullable = false)
    private boolean avgBuyPriceModified;

    // 평단가 기준 화폐
    @Column(nullable = false)
    private String unitCurrency; // KRW

    // User 와 1대 n 매칭
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected void setUser(User user) {
        this.user = user;
    }

    public void updateBalance(double newBalance) {
        this.balance = newBalance;
    }

    public void updateLocked(double newLocked) {
        this.locked = newLocked;
    }
}
