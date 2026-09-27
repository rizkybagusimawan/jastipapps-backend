package com.jastipapps.auth_service.repository;

import com.jastipapps.auth_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByStatus(String status, Pageable pageable);

    

    @Query("""
        SELECT p FROM Product p
        WHERE p.status = :status
        AND (:search IS NULL OR LOWER(p.namaProduk) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
        AND (:kategori IS NULL OR p.kategori = :kategori)
        """)
    Page<Product> searchProducts(
        @Param("status") String status,
        @Param("search") String search,
        @Param("kategori") String kategori,
        Pageable pageable
    );
}