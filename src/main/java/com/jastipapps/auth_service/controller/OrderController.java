package com.jastipapps.auth_service.controller;

import com.jastipapps.auth_service.dto.OrderDtos.*;
import com.jastipapps.auth_service.security.JwtUtil;
import com.jastipapps.auth_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = UUID.fromString(jwtUtil.extractUserId(authHeader.replace("Bearer ", "")));
        return ResponseEntity.ok(orderService.createOrder(req, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = UUID.fromString(jwtUtil.extractUserId(authHeader.replace("Bearer ", "")));
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderId
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(status, orderId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest req
    ) {
        orderService.updateOrderStatus(id, req.status(), req.keteranganStatus(), req.buktiFotoUrl());
        return ResponseEntity.ok("Status order berhasil diubah");
    }
}