package com.jastipapps.auth_service.repository;

import com.jastipapps.auth_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findAllByStatusOrderByCreatedAtDesc(String status);

        @Query("""
        SELECT o FROM Order o
        WHERE (:status IS NULL OR o.status = :status)
        AND (:orderIdSearch IS NULL OR LOWER(CAST(o.id AS string)) LIKE LOWER(CONCAT('%', CAST(:orderIdSearch AS string), '%')))
        ORDER BY o.createdAt DESC
        """)
    List<Order> searchOrders(
        @org.springframework.data.repository.query.Param("status") String status,
        @org.springframework.data.repository.query.Param("orderIdSearch") String orderIdSearch
    );
}