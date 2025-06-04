package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.order.request.OrderCreateRequestDto;
import org.example.backend.dto.order.request.OrderUpdateRequestDto;
import org.example.backend.dto.order.response.OrderHistoryResponseDto;
import org.example.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderHistoryResponseDto> createOrder(@RequestBody OrderCreateRequestDto dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    // 주문 수정
    @PutMapping
    public ResponseEntity<OrderHistoryResponseDto> updateOrder(@RequestBody OrderUpdateRequestDto dto) {
        return ResponseEntity.ok(orderService.updateOrder(dto));
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // 주문 체결 시도 (PENDING 주문만)
    @PostMapping("/pending/resolve")
    public ResponseEntity<List<OrderHistoryResponseDto>> resolvePendingOrders() {
        return ResponseEntity.ok(orderService.resolvePendingOrders());
    }

    // 주문 내역 조회
    @GetMapping
    public ResponseEntity<List<OrderHistoryResponseDto>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }
}
