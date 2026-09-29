package com.jastipapps.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_messages")
@Data
public class OrderMessage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "sender_role", nullable = false)
    private String senderRole;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String message;

    @Column(name = "image_url", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}