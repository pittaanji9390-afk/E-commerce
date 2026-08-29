package com.marketplace.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final boolean active;
    private final boolean emailVerified;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UUID id, String email, String passwordHash, String firstName, String lastName, boolean active, boolean emailVerified, Set<String> roles) {
        Set<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .password(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .active(active)
                .emailVerified(emailVerified)
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public boolean hasRole(RoleEnum role) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(role.name()));
    }
}
