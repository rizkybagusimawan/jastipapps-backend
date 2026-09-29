package com.jastipapps.auth_service.controller;

import com.jastipapps.auth_service.security.JwtUtil;
import com.jastipapps.auth_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders/my/messages")
@RequiredArgsConstructor
public class ChatUnreadController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    @GetMapping("/unread-counts")
    public ResponseEntity<Map<UUID, Long>> getUnreadCounts(
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = UUID.fromString(
                jwtUtil.extractUserId(
                        authHeader.replace("Bearer ", "")
                )
        );

        return ResponseEntity.ok(
                chatService.getUnreadCounts(userId)
        );
    }
}