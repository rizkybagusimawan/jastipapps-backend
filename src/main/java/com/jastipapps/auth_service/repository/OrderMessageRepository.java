package com.jastipapps.auth_service.repository;

import com.jastipapps.auth_service.entity.OrderMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderMessageRepository extends JpaRepository<OrderMessage, UUID> {
    List<OrderMessage> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}