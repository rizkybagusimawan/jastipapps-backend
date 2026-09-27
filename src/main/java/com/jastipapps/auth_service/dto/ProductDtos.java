package com.jastipapps.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductDtos {

    public record CreateProductRequest(
        @NotBlank String namaProduk,
        String deskripsi,
        String kategori,
        String fotoUrl,
        String sumber,
        @NotNull @PositiveOrZero BigDecimal hargaAsli,
        @NotNull @PositiveOrZero BigDecimal biayaJasa,
        Integer kuota
    ) {}

    public record ProductResponse(
        UUID id,
        String namaProduk,
        String deskripsi,
        String kategori,
        String fotoUrl,
        String sumber,
        BigDecimal hargaAsli,
        BigDecimal biayaJasa,
        BigDecimal hargaTotal,
        String status,
        Integer kuota
    ) {}
}