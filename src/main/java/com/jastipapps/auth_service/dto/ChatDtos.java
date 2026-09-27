package com.jastipapps.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatDtos {

    public record SendMessageRequest(
        @NotBlank String message
    ) {}

    public record MessageResponse(
        UUID id,
        UUID senderId,
        String senderRole,
        String senderName,
        String message,
        OffsetDateTime createdAt,
        boolean isMine
    ) {}
}