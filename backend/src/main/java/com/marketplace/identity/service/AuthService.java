package com.marketplace.identity.service;

import com.marketplace.identity.domain.RefreshToken;
import com.marketplace.identity.domain.Role;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.domain.UserStatus;
import com.marketplace.identity.dto.*;
import com.marketplace.identity.repository.RefreshTokenRepository;
import com.marketplace.identity.repository.RoleRepository;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.security.JwtTokenProvider;
import com.marketplace.security.RoleEnum;
import com.marketplace.security.SecurityConstants;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import com.marketplace.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${marketplace.security.jwt.access-token-expiration-seconds:900}")
    private long accessTokenExpirationSeconds;

    @Value("${marketplace.security.jwt.refresh-token-expiration-seconds:604800}")
    private long refreshTokenExpirationSeconds;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Email address is already registered.");
        }

        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName(RoleEnum.ROLE_CUSTOMER.name())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleEnum.ROLE_CUSTOMER.name())
                        .description("Customer Buyer")
                        .build()));
        roles.add(customerRole);

        if (request.isRegisterAsSeller()) {
            Role sellerRole = roleRepository.findByName(RoleEnum.ROLE_SELLER.name())
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(RoleEnum.ROLE_SELLER.name())
                            .description("Merchant Vendor")
                            .build()));
            roles.add(sellerRole);
        }

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .emailVerified(true) // Defaults to true for streamlined flow
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user: [id={}, email={}, roles={}]", savedUser.getId(), savedUser.getEmail(), savedUser.getRoles().stream().map(Role::getName).toList());

        return generateAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!user.isActive()) {
            throw new BusinessRuleException(ErrorCode.UNAUTHORIZED, "Your account is " + user.getStatus() + ". Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);
            throw new UnauthorizedException("Invalid email or password.");
        }

        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token."));

        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token is expired or revoked. Please log in again.");
        }

        // Revoke the current refresh token (Token Rotation Security)
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Logged out and revoked all refresh tokens for user: {}", userId);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Current password does not match.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Password updated successfully for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return toUserDto(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roleNames);

        // Generate persistent Refresh Token
        String rawRefreshToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(rawRefreshToken)
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType(SecurityConstants.TOKEN_PREFIX.trim())
                .expiresIn(accessTokenExpirationSeconds)
                .user(toUserDto(user))
                .build();
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .mfaEnabled(user.isMfaEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
