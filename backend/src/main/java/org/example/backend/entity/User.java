package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    // 객체에 접근하기 위한 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 회원가입을 위한 id
    @Column(unique = true, nullable = false)
    private String username;

    // 회원가입을 위한 password
    @Column(nullable = false)
    private String password;

    // 회원가입을 위한 email
    @Column(nullable = false, unique = true)
    private String email;

    // 보유 원화 잔고
    private double balance;

    // 보유 자산과 1대 n 매칭
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    // 거래내역과 1대 n 매칭
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Fill> fills = new ArrayList<>();

    // 거래 방법과 1대 n 매칭
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    public void updateBalance(double newBalance) {
        this.balance = newBalance;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setUser(this);
    }
}
