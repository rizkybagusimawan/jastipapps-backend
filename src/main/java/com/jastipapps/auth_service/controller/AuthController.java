package com.jastipapps.auth_service.controller;

import com.jastipapps.auth_service.dto.AuthDtos;
import com.jastipapps.auth_service.dto.AuthDtos.*;
import com.jastipapps.auth_service.security.JwtUtil;
import com.jastipapps.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok("Instruksi reset password telah dikirim");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok("Password berhasil diubah");
    }
        @GetMapping("/me")
    public ResponseEntity<AuthDtos.UserProfileResponse> getProfile(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<AuthDtos.UserProfileResponse> updateProfile(
            @Valid @RequestBody AuthDtos.UpdateProfileRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(authService.updateProfile(userId, req));
    }
}