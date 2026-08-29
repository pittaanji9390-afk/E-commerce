package com.marketplace.identity.dto;

import com.marketplace.identity.domain.UserStatus;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private UserStatus status;
    private boolean emailVerified;
    private boolean mfaEnabled;
    private Set<String> roles;
    private Instant createdAt;
}
