package com.jastipapps.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "nama_produk", nullable = false)
    private String namaProduk;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    private String kategori;

    @Column(name = "foto_url")
    private String fotoUrl;

    private String sumber;

    @Column(name = "harga_asli", nullable = false)
    private BigDecimal hargaAsli;

    @Column(name = "biaya_jasa", nullable = false)
    private BigDecimal biayaJasa;

    // Kolom generated, hanya dibaca, tidak pernah di-set manual dari Java
    @Column(name = "harga_total", insertable = false, updatable = false)
    private BigDecimal hargaTotal;

    @Column(nullable = false)
    private String status = "tersedia";

    @Column(nullable = false)
    private Integer kuota = 0;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}