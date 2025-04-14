package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.entity.enums.CurrencyType;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "account")
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal locked;

    @Column(nullable = false)
    private BigDecimal avgBuyPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected void setUser(User user) {
        this.user = user;
    }

    public void updateQuantity(BigDecimal newQuantity) {
        this.quantity = newQuantity;
    }

    public void updateLocked(BigDecimal newLocked) {
        this.locked = newLocked;
    }
}
