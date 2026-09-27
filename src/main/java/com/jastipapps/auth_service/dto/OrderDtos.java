package com.jastipapps.auth_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class OrderDtos {

    public record CreateOrderRequest(
        @NotNull UUID productId,
        @Min(1) int jumlah,
        String catatan
    ) {}

    public record UpdateOrderStatusRequest(
        @jakarta.validation.constraints.NotBlank String status,
        String keteranganStatus,
        String buktiFotoUrl
    ) {}

    public record OrderResponse(
        UUID id, UUID productId, String namaProduk, String fotoUrl,
        int jumlah, String catatan, BigDecimal hargaSatuan, BigDecimal totalHarga,
        String status, OffsetDateTime createdAt,
        String keteranganStatus, String buktiFotoUrl
    ) {}

    public record AdminOrderResponse(
        UUID id, UUID productId, String namaProduk, String fotoUrl,
        int jumlah, String catatan, BigDecimal hargaSatuan, BigDecimal totalHarga,
        String status, OffsetDateTime createdAt, String userFullName, String userEmail,
        String keteranganStatus, String buktiFotoUrl
    ) {}
}