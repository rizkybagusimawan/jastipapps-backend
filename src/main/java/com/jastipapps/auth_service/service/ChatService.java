package com.jastipapps.auth_service.service;

import com.jastipapps.auth_service.dto.ChatDtos.*;
import com.jastipapps.auth_service.entity.Order;
import com.jastipapps.auth_service.entity.OrderMessage;
import com.jastipapps.auth_service.entity.User;
import com.jastipapps.auth_service.repository.OrderMessageRepository;
import com.jastipapps.auth_service.repository.OrderRepository;
import com.jastipapps.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OrderMessageRepository messageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(UUID orderId, String messageText, UUID senderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order tidak ditemukan"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        // Cek akses: hanya pemilik order atau admin yang boleh chat
        boolean isOwner = order.getUserId().equals(senderId);
        boolean isAdmin = "admin".equals(sender.getRole());
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("Kamu tidak punya akses ke chat order ini");
        }

        OrderMessage msg = new OrderMessage();
        msg.setOrderId(orderId);
        msg.setSenderId(senderId);
        msg.setSenderRole(sender.getRole());
        msg.setMessage(messageText);
        msg.setCreatedAt(OffsetDateTime.now());

        OrderMessage saved = messageRepository.save(msg);
        return toResponse(saved, sender, senderId);
    }

    public List<MessageResponse> getMessages(UUID orderId, UUID requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order tidak ditemukan"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        boolean isOwner = order.getUserId().equals(requesterId);
        boolean isAdmin = "admin".equals(requester.getRole());
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("Kamu tidak punya akses ke chat order ini");
        }

        return messageRepository.findByOrderIdOrderByCreatedAtAsc(orderId).stream()
                .map(msg -> {
                    User sender = userRepository.findById(msg.getSenderId()).orElse(null);
                    return toResponse(msg, sender, requesterId);
                })
                .toList();
    }

    private MessageResponse toResponse(OrderMessage msg, User sender, UUID viewerId) {
        return new MessageResponse(
                msg.getId(),
                msg.getSenderId(),
                msg.getSenderRole(),
                sender != null ? sender.getFullName() : "Unknown",
                msg.getMessage(),
                msg.getCreatedAt(),
                msg.getSenderId().equals(viewerId)
        );
    }
}