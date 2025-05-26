package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.order.request.OrderCreateRequestDto;
import org.example.backend.dto.order.response.OrderResponseDto;
import org.example.backend.dto.order.request.ProfitRequestDto;
import org.example.backend.dto.order.response.ProfitResponseDto;
import org.example.backend.entity.User;
import org.example.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderCreateRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(orderService.createOrder(requestDto, user));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(
            @AuthenticationPrincipal User user
    ) {
        List<OrderResponseDto> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/profit")
    public ResponseEntity<ProfitResponseDto> calculateProfit(
            @Valid @RequestBody ProfitRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        ProfitResponseDto result = orderService.calculateProfit(dto.getOrderId(), dto.getCurrentPrice(), user);
        return ResponseEntity.ok(result);
    }
}