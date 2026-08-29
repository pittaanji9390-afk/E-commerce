package com.marketplace.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("sellerSecurity")
public class SellerSecurityService {

    public boolean isSellerOwner(Authentication authentication, UUID sellerId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            // Admins can bypass seller tenant ownership for governance/moderation
            if (userPrincipal.hasRole(RoleEnum.ROLE_ADMIN) || userPrincipal.hasRole(RoleEnum.ROLE_SUPER_ADMIN)) {
                return true;
            }
            return userPrincipal.getId().equals(sellerId);
        }
        return false;
    }

    public boolean isCustomerOwner(Authentication authentication, UUID customerId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            if (userPrincipal.hasRole(RoleEnum.ROLE_ADMIN) || userPrincipal.hasRole(RoleEnum.ROLE_SUPER_ADMIN)) {
                return true;
            }
            return userPrincipal.getId().equals(customerId);
        }
        return false;
    }
}
