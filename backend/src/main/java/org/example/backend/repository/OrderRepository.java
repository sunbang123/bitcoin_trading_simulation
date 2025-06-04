package org.example.backend.repository;

import org.example.backend.entity.Order;
import org.example.backend.entity.User;
import org.example.backend.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findAllByUser(User user);
    List<Order> findByUserAndOrderStatus(User user, OrderStatus orderStatus);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
