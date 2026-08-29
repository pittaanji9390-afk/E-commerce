package com.marketplace.cart.service;

import com.marketplace.cart.domain.Cart;
import com.marketplace.cart.domain.CartItem;
import com.marketplace.cart.dto.*;
import com.marketplace.cart.repository.CartItemRepository;
import com.marketplace.cart.repository.CartRepository;
import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.inventory.domain.Inventory;
import com.marketplace.inventory.repository.InventoryRepository;
import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductImage;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.repository.ProductImageRepository;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerStatus;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerService customerService;

    @Transactional
    public CartDto getOrCreateCart(UUID customerId, String sessionId) {
        Cart cart = resolveCart(customerId, sessionId);
        return toCartDto(cart);
    }

    @Transactional
    public CartDto addItem(UUID customerId, AddToCartRequest request) {
        Cart cart = resolveCart(customerId, request.getSessionId());

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));

        Product product = variant.getProduct();
        Seller seller = product.getSeller();

        // Zero-Trust Validations
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Product is currently not active for purchase.");
        }

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Merchant store is currently inactive.");
        }

        Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", variant.getId()));

        int requestedTotalQty = request.getQuantity();
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());
        if (existingItemOpt.isPresent()) {
            requestedTotalQty += existingItemOpt.get().getQuantity();
        }

        if (inventory.getAvailable() < requestedTotalQty) {
            throw new BusinessRuleException(ErrorCode.INSUFFICIENT_INVENTORY,
                    "Cannot add to cart. Only " + inventory.getAvailable() + " units available.");
        }

        BigDecimal currentUnitPrice = variant.getEffectivePrice();

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            existing.setQuantity(requestedTotalQty);
            existing.setUnitPriceSnapshot(currentUnitPrice);
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .seller(seller)
                    .quantity(request.getQuantity())
                    .unitPriceSnapshot(currentUnitPrice)
                    .build();
            cart.addItem(newItem);
            cartRepository.save(cart);
        }

        return toCartDto(cart);
    }

    @Transactional
    public CartDto updateItemQuantity(UUID customerId, UUID cartItemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        Inventory inventory = inventoryRepository.findByVariantId(item.getVariant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", item.getVariant().getId()));

        if (inventory.getAvailable() < request.getQuantity()) {
            throw new BusinessRuleException(ErrorCode.INSUFFICIENT_INVENTORY,
                    "Cannot update quantity. Only " + inventory.getAvailable() + " units available.");
        }

        item.setQuantity(request.getQuantity());
        item.setUnitPriceSnapshot(item.getVariant().getEffectivePrice());
        cartItemRepository.save(item);

        return toCartDto(item.getCart());
    }

    @Transactional
    public CartDto removeItem(UUID customerId, UUID cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        Cart cart = item.getCart();
        cart.removeItem(item);
        cartItemRepository.delete(item);

        return toCartDto(cart);
    }

    @Transactional
    public void clearCart(UUID customerId) {
        cartRepository.findByCustomerId(customerId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart resolveCart(UUID customerId, String sessionId) {
        if (customerId != null) {
            Customer customer = customerService.getOrCreateCustomer(customerId);
            return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
                Cart newCart = Cart.builder()
                        .customer(customer)
                        .build();
                return cartRepository.save(newCart);
            });
        } else if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionId(sessionId).orElseGet(() -> {
                Cart newCart = Cart.builder()
                        .sessionId(sessionId)
                        .build();
                return cartRepository.save(newCart);
            });
        }
        throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Either authenticated customer or sessionId is required for cart operations.");
    }

    public CartDto toCartDto(Cart cart) {
        Map<Seller, List<CartItem>> groupedBySeller = cart.getItems().stream()
                .collect(Collectors.groupingBy(CartItem::getSeller));

        List<VendorCartGroupDto> vendorGroups = new ArrayList<>();
        BigDecimal overallSubtotal = BigDecimal.ZERO;
        int totalItemCount = 0;

        for (Map.Entry<Seller, List<CartItem>> entry : groupedBySeller.entrySet()) {
            Seller seller = entry.getKey();
            List<CartItem> items = entry.getValue();

            BigDecimal groupSubtotal = BigDecimal.ZERO;
            List<CartItemDto> itemDtos = new ArrayList<>();

            for (CartItem item : items) {
                ProductVariant variant = item.getVariant();
                Product product = variant.getProduct();
                BigDecimal livePrice = variant.getEffectivePrice(); // Fresh price snapshot
                BigDecimal itemTotal = livePrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                groupSubtotal = groupSubtotal.add(itemTotal);
                totalItemCount += item.getQuantity();

                String imgUrl = imageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId())
                        .stream().findFirst().map(ProductImage::getImageUrl).orElse(null);

                int available = inventoryRepository.findByVariantId(variant.getId())
                        .map(Inventory::getAvailable).orElse(0);

                itemDtos.add(CartItemDto.builder()
                        .itemId(item.getId())
                        .variantId(variant.getId())
                        .productId(product.getId())
                        .productTitle(product.getTitle())
                        .variantTitle(variant.getTitle())
                        .sku(variant.getSku())
                        .unitPrice(livePrice)
                        .quantity(item.getQuantity())
                        .itemTotal(itemTotal)
                        .imageUrl(imgUrl)
                        .availableStock(available)
                        .build());
            }

            overallSubtotal = overallSubtotal.add(groupSubtotal);

            vendorGroups.add(VendorCartGroupDto.builder()
                    .sellerId(seller.getId())
                    .sellerName(seller.getDisplayName())
                    .sellerSlug(seller.getStoreSlug())
                    .items(itemDtos)
                    .groupSubtotal(groupSubtotal)
                    .build());
        }

        return CartDto.builder()
                .id(cart.getId())
                .vendorGroups(vendorGroups)
                .subtotal(overallSubtotal)
                .totalItemCount(totalItemCount)
                .build();
    }
}
