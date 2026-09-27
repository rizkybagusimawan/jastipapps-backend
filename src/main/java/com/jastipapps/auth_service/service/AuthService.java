package com.jastipapps.auth_service.service;

import com.jastipapps.auth_service.dto.AuthDtos;
import com.jastipapps.auth_service.dto.AuthDtos.*;
import com.jastipapps.auth_service.entity.PasswordReset;
import com.jastipapps.auth_service.entity.User;
import com.jastipapps.auth_service.repository.PasswordResetRepository;
import com.jastipapps.auth_service.repository.UserRepository;
import com.jastipapps.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setPhoneNumber(req.phoneNumber());
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email atau password salah"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email atau password salah");
        }

        String token = jwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
    }

    public void forgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email tidak ditemukan"));

        PasswordReset reset = new PasswordReset();
        reset.setUserId(user.getId());
        reset.setToken(UUID.randomUUID().toString());
        reset.setExpiresAt(OffsetDateTime.now().plusMinutes(30));
        reset.setCreatedAt(OffsetDateTime.now());

        passwordResetRepository.save(reset);

        // TODO: kirim email berisi reset.getToken() ke user.getEmail()
        // Sementara ini bisa dicek langsung dari database saat testing
    }

    public void resetPassword(ResetPasswordRequest req) {
        PasswordReset reset = passwordResetRepository.findByToken(req.token())
                .orElseThrow(() -> new IllegalArgumentException("Token tidak valid"));

        if (reset.getIsUsed() || reset.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Token sudah tidak berlaku");
        }

        User user = userRepository.findById(reset.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        reset.setIsUsed(true);
        passwordResetRepository.save(reset);
    }
        public AuthDtos.UserProfileResponse getProfile(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

                return new AuthDtos.UserProfileResponse(
                user.getId().toString(), user.getEmail(), user.getFullName(),
                user.getPhoneNumber(), user.getRole()
        );
    }

    public AuthDtos.UserProfileResponse updateProfile(String userId, AuthDtos.UpdateProfileRequest req) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        user.setFullName(req.fullName());
        user.setPhoneNumber(req.phoneNumber());
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

                return new AuthDtos.UserProfileResponse(
                user.getId().toString(), user.getEmail(), user.getFullName(),
                user.getPhoneNumber(), user.getRole()
        );
    }
}