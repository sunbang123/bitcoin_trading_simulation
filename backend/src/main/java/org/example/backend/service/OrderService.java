package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Order;
import org.example.backend.entity.User;
import org.example.backend.entity.enums.OrderState;
import org.example.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order placeOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getUserPendingOrders(Long userId) {
        return orderRepository.findByUserIdAndState(userId, OrderState.WAIT);
    }

    public void cancelOrder(Order order) {
        order.cancel();
        orderRepository.save(order);
    }
}
