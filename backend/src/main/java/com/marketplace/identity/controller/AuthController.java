package com.marketplace.identity.controller;

import com.marketplace.identity.dto.*;
import com.marketplace.identity.service.AuthService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication & Identity", description = "Endpoints for registration, login, token rotation, and password management")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user account (Customer or Seller)")
    @PostMapping("/register")
    public ResponseEntity<Result<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.ok(response, "Account registered successfully."));
    }

    @Operation(summary = "Authenticate with email and password")
    @PostMapping("/login")
    public ResponseEntity<Result<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(Result.ok(response, "Authenticated successfully."));
    }

    @Operation(summary = "Rotate refresh token and retrieve new access token")
    @PostMapping("/refresh")
    public ResponseEntity<Result<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(Result.ok(response, "Token refreshed successfully."));
    }

    @Operation(summary = "Logout user and revoke active sessions")
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null) {
            authService.logout(principal.getId());
        }
        return ResponseEntity.ok(Result.ok(null, "Logged out successfully."));
    }

    @Operation(summary = "Change password for authenticated user")
    @PostMapping("/change-password")
    public ResponseEntity<Result<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(Result.ok(null, "Password changed successfully."));
    }

    @Operation(summary = "Retrieve current authenticated user profile")
    @GetMapping("/me")
    public ResponseEntity<Result<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserDto user = authService.getCurrentUser(principal.getId());
        return ResponseEntity.ok(Result.ok(user));
    }
}
