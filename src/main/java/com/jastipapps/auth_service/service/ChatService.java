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
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OrderMessageRepository messageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(
            UUID orderId,
            SendMessageRequest req,
            UUID senderId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order tidak ditemukan"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User tidak ditemukan"));

        boolean isOwner = order.getUserId().equals(senderId);
        boolean isAdmin = "admin".equals(sender.getRole());

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException(
                    "Kamu tidak punya akses ke chat order ini"
            );
        }

        OrderMessage msg = new OrderMessage();

        msg.setOrderId(orderId);
        msg.setSenderId(senderId);
        msg.setSenderRole(sender.getRole());
        msg.setMessage(req.message());
        msg.setImageUrl(req.imageUrl());
        msg.setCreatedAt(OffsetDateTime.now());

        OrderMessage saved = messageRepository.save(msg);

        return toResponse(saved, sender, senderId);
    }

    @Transactional
    public List<MessageResponse> getMessages(
            UUID orderId,
            UUID requesterId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order tidak ditemukan"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User tidak ditemukan"));

        boolean isOwner = order.getUserId().equals(requesterId);
        boolean isAdmin = "admin".equals(requester.getRole());

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException(
                    "Kamu tidak punya akses ke chat order ini"
            );
        }

        /*
         * Ambil semua pesan terlebih dahulu.
         */
        List<OrderMessage> messages =
                messageRepository.findByOrderIdOrderByCreatedAtAsc(orderId);

        /*
         * Pesan dari lawan chat yang belum dibaca
         * langsung ditandai sebagai sudah dibaca.
         */
        List<OrderMessage> unreadMessages =
                messages.stream()
                        .filter(message ->
                                !message.getSenderId().equals(requesterId)
                                        && message.getReadAt() == null
                        )
                        .toList();

        if (!unreadMessages.isEmpty()) {

            OffsetDateTime now = OffsetDateTime.now();

            unreadMessages.forEach(message ->
                    message.setReadAt(now)
            );

            messageRepository.saveAll(unreadMessages);
        }

        /*
         * Return response setelah read_at diperbarui.
         */
        return messages.stream()
                .map(msg -> {

                    User sender = userRepository
                            .findById(msg.getSenderId())
                            .orElse(null);

                    return toResponse(
                            msg,
                            sender,
                            requesterId
                    );
                })
                .toList();
    }

    @Transactional
    public void markMessagesAsRead(
            UUID orderId,
            UUID requesterId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order tidak ditemukan"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User tidak ditemukan"));

        boolean isOwner = order.getUserId().equals(requesterId);
        boolean isAdmin = "admin".equals(requester.getRole());

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException(
                    "Kamu tidak punya akses ke chat order ini"
            );
        }

        List<OrderMessage> unreadMessages =
                messageRepository
                        .findByOrderIdAndSenderIdNotAndReadAtIsNull(
                                orderId,
                                requesterId
                        );

        if (unreadMessages.isEmpty()) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();

        unreadMessages.forEach(message ->
                message.setReadAt(now)
        );

        messageRepository.saveAll(unreadMessages);
    }

    public long getUnreadCount(
            UUID orderId,
            UUID requesterId
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order tidak ditemukan"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User tidak ditemukan"));

        boolean isOwner = order.getUserId().equals(requesterId);
        boolean isAdmin = "admin".equals(requester.getRole());

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException(
                    "Kamu tidak punya akses ke chat order ini"
            );
        }

        return messageRepository
                .countByOrderIdAndSenderIdNotAndReadAtIsNull(
                        orderId,
                        requesterId
                );
    }

    public Map<UUID, Long> getUnreadCounts(UUID userId) {

        List<Object[]> results =
                messageRepository.findUnreadCountsByUserId(userId);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> ((Number) row[1]).longValue()
                ));
    }

    private MessageResponse toResponse(
            OrderMessage msg,
            User sender,
            UUID viewerId
    ) {
        return new MessageResponse(
                msg.getId(),
                msg.getSenderId(),
                msg.getSenderRole(),
                sender != null
                        ? sender.getFullName()
                        : "Unknown",
                msg.getMessage(),
                msg.getImageUrl(),
                msg.getCreatedAt(),
                msg.getReadAt(),
                msg.getSenderId().equals(viewerId)
        );
    }
}