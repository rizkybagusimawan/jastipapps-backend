package com.jastipapps.auth_service.dto;

import jakarta.validation.constraints.AssertTrue;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatDtos {

    public record SendMessageRequest(
            String message,
            String imageUrl
    ) {

        @AssertTrue(message = "Pesan atau gambar harus diisi")
        public boolean isValidMessage() {
            return (message != null && !message.isBlank())
                    || (imageUrl != null && !imageUrl.isBlank());
        }
    }

    public record MessageResponse(
            UUID id,
            UUID senderId,
            String senderRole,
            String senderName,
            String message,
            String imageUrl,
            OffsetDateTime createdAt,
            OffsetDateTime readAt,
            boolean isMine
    ) {}

    public record UnreadCountResponse(
            long count
    ) {}
}