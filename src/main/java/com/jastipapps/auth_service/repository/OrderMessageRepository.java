package com.jastipapps.auth_service.repository;

import com.jastipapps.auth_service.entity.OrderMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderMessageRepository extends JpaRepository<OrderMessage, UUID> {

    List<OrderMessage> findByOrderIdOrderByCreatedAtAsc(UUID orderId);

    List<OrderMessage> findByOrderIdAndSenderIdNotAndReadAtIsNull(
            UUID orderId,
            UUID senderId
    );

    long countByOrderIdAndSenderIdNotAndReadAtIsNull(
            UUID orderId,
            UUID senderId
    );

    @Query("""
    SELECT m.orderId, COUNT(m)
    FROM OrderMessage m
    WHERE m.senderId <> :userId
      AND m.readAt IS NULL
    GROUP BY m.orderId
""")
List<Object[]> findUnreadCountsByUserId(
        @Param("userId") UUID userId
);

    
}