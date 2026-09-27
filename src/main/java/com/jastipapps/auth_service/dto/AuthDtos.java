package com.jastipapps.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String fullName,
        String phoneNumber
    ) {}

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record ForgotPasswordRequest(
        @NotBlank @Email String email
    ) {}

    public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword
    ) {}

    public record AuthResponse(
        String token,
        String email,
        String fullName,
        String role
    ) {}

    public record UserProfileResponse(
        String id,
        String email,
        String fullName,
        String phoneNumber,
        String role
    ) {}

    public record UpdateProfileRequest(
        @NotBlank String fullName,
        String phoneNumber
    ) {}
}