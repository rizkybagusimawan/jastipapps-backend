package com.jastipapps.auth_service.controller;

import com.jastipapps.auth_service.dto.ChatDtos.*;
import com.jastipapps.auth_service.security.JwtUtil;
import com.jastipapps.auth_service.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders/{orderId}/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    private UUID extractUserId(String authHeader) {
        return UUID.fromString(
                jwtUtil.extractUserId(
                        authHeader.replace("Bearer ", "")
                )
        );
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID orderId,
            @Valid @RequestBody SendMessageRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID senderId = extractUserId(authHeader);

        return ResponseEntity.ok(
                chatService.sendMessage(
                        orderId,
                        req,
                        senderId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID requesterId = extractUserId(authHeader);

        return ResponseEntity.ok(
                chatService.getMessages(
                        orderId,
                        requesterId
                )
        );
    }

    @PatchMapping("/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID requesterId = extractUserId(authHeader);

        chatService.markMessagesAsRead(
                orderId,
                requesterId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID requesterId = extractUserId(authHeader);

        long count = chatService.getUnreadCount(
                orderId,
                requesterId
        );

        return ResponseEntity.ok(
                new UnreadCountResponse(count)
        );
    }
}