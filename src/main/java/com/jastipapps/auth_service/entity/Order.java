package com.jastipapps.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Integer jumlah = 1;

    @Column(columnDefinition = "TEXT")
    private String catatan;

    @Column(name = "harga_satuan", nullable = false)
    private BigDecimal hargaSatuan;

    @Column(name = "total_harga", insertable = false, updatable = false)
    private BigDecimal totalHarga;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "keterangan_status", columnDefinition = "TEXT")
    private String keteranganStatus;

    @Column(name = "bukti_foto_url")
    private String buktiFotoUrl;
}