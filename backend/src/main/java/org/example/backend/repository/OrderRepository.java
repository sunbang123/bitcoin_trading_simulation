package org.example.backend.repository;

import org.example.backend.entity.Order;
import org.example.backend.entity.enums.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndState(Long userId, OrderState state);
}
