package com.marketplace.cart.controller;

import com.marketplace.cart.dto.AddToCartRequest;
import com.marketplace.cart.dto.CartDto;
import com.marketplace.cart.dto.UpdateCartItemRequest;
import com.marketplace.cart.service.CartService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Shopping Cart", description = "Endpoints for customer and guest multi-vendor shopping cart")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get current cart contents grouped by vendor")
    @GetMapping
    public ResponseEntity<Result<CartDto>> getCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String sessionId) {
        UUID customerId = principal != null ? principal.getId() : null;
        CartDto cart = cartService.getOrCreateCart(customerId, sessionId);
        return ResponseEntity.ok(Result.ok(cart));
    }

    @Operation(summary = "Add an item to the shopping cart")
    @PostMapping("/items")
    public ResponseEntity<Result<CartDto>> addItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddToCartRequest request) {
        UUID customerId = principal != null ? principal.getId() : null;
        CartDto cart = cartService.addItem(customerId, request);
        return ResponseEntity.ok(Result.ok(cart, "Item added to cart."));
    }

    @Operation(summary = "Update quantity for a cart item")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Result<CartDto>> updateQuantity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        UUID customerId = principal != null ? principal.getId() : null;
        CartDto cart = cartService.updateItemQuantity(customerId, itemId, request);
        return ResponseEntity.ok(Result.ok(cart, "Cart item updated."));
    }

    @Operation(summary = "Remove an item from cart")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Result<CartDto>> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID itemId) {
        UUID customerId = principal != null ? principal.getId() : null;
        CartDto cart = cartService.removeItem(customerId, itemId);
        return ResponseEntity.ok(Result.ok(cart, "Item removed from cart."));
    }

    @Operation(summary = "Clear all items in cart")
    @DeleteMapping
    public ResponseEntity<Result<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null) {
            cartService.clearCart(principal.getId());
        }
        return ResponseEntity.ok(Result.ok(null, "Cart cleared."));
    }
}
