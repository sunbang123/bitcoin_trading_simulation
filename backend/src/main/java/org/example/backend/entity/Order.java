package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.entity.enums.OrderState;
import org.example.backend.entity.enums.OrderType;
import org.example.backend.entity.enums.Side;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "`order`")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String market;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType ordType;

    private BigDecimal price;

    private BigDecimal volume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Fill> fills = new ArrayList<>();

    public static Order create(String market, Side side, OrderType ordType, BigDecimal price, BigDecimal volume, User user) {
        return Order.builder()
                .market(market)
                .side(side)
                .ordType(ordType)
                .price(price)
                .volume(volume)
                .state(OrderState.WAIT)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    public void complete() {
        this.state = OrderState.DONE;
    }

    public void cancel() {
        this.state = OrderState.CANCEL;
    }
}
