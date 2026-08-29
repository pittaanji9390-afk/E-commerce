package com.marketplace.customer.controller;

import com.marketplace.customer.dto.*;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Customer Accounts", description = "Endpoints for managing customer profile, addresses, and preferences")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Retrieve current customer profile and address book")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<CustomerProfileDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        CustomerProfileDto profile = customerService.getProfile(principal.getId());
        return ResponseEntity.ok(Result.ok(profile));
    }

    @Operation(summary = "Update customer personal profile and preferences")
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<CustomerProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        CustomerProfileDto updated = customerService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(Result.ok(updated, "Profile updated successfully."));
    }

    @Operation(summary = "Add a new address to customer address book")
    @PostMapping("/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<AddressDto>> addAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAddressRequest request) {
        AddressDto address = customerService.addAddress(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.ok(address, "Address added successfully."));
    }

    @Operation(summary = "Delete an address from customer address book")
    @DeleteMapping("/addresses/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        customerService.deleteAddress(principal.getId(), id);
        return ResponseEntity.ok(Result.ok(null, "Address removed successfully."));
    }
}
