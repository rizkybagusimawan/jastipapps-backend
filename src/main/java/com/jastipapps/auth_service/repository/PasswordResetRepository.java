package com.jastipapps.auth_service.repository;

import com.jastipapps.auth_service.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, java.util.UUID> {
    Optional<PasswordReset> findByToken(String token);
}