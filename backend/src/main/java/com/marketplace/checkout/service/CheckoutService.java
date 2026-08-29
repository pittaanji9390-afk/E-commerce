package com.marketplace.checkout.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.cart.domain.Cart;
import com.marketplace.cart.domain.CartItem;
import com.marketplace.cart.repository.CartRepository;
import com.marketplace.coupon.dto.ApplyCouponRequest;
import com.marketplace.coupon.dto.CouponDiscountResult;
import com.marketplace.coupon.service.CouponService;
import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.inventory.service.InventoryService;
import com.marketplace.order.domain.*;
import com.marketplace.order.dto.CheckoutCalculationDto;
import com.marketplace.order.dto.CreateOrderRequest;
import com.marketplace.order.dto.OrderDto;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.service.OrderService;
import com.marketplace.pricing.service.TaxCalculationService;
import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final CouponService couponService;
    private final TaxCalculationService taxService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderDto processCheckout(UUID customerId, CreateOrderRequest request) {
        Customer customer = customerService.getOrCreateCustomer(customerId);

        // Idempotency check: prevent duplicate submissions
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Idempotent checkout hit: returning existing order {}", existing.get().getOrderNumber());
                return orderService.getOrderById(existing.get().getId());
            }
        }

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "No active cart found for checkout."));

        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Cannot checkout an empty cart.");
        }

        // 1. Calculate Authoritative Prices & Group by Seller
        Map<Seller, List<CartItem>> sellerGroups = cart.getItems().stream()
                .collect(Collectors.groupingBy(CartItem::getSeller));

        BigDecimal totalSubtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            BigDecimal livePrice = item.getVariant().getEffectivePrice();
            totalSubtotal = totalSubtotal.add(livePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 2. Validate Coupon
        BigDecimal totalDiscount = BigDecimal.ZERO;
        UUID couponId = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponDiscountResult couponResult = couponService.validateAndCalculateDiscount(
                    customerId,
                    ApplyCouponRequest.builder()
                            .code(request.getCouponCode())
                            .cartSubtotal(totalSubtotal)
                            .build()
            );
            totalDiscount = couponResult.getDiscountAmount();
            couponId = couponResult.getCouponId();
        }

        // 3. Calculate Taxes & Shipping
        String state = request.getShippingAddress().getStateProvince();
        String country = request.getShippingAddress().getCountryCode();
        BigDecimal taxableSubtotal = totalSubtotal.subtract(totalDiscount).max(BigDecimal.ZERO);
        BigDecimal totalTax = taxService.calculateTax(taxableSubtotal, country, state);
        BigDecimal totalShipping = BigDecimal.ZERO; // Promotional free shipping
        BigDecimal grandTotal = taxableSubtotal.add(totalTax).add(totalShipping).setScale(2, RoundingMode.HALF_EVEN);

        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (100 + new Random().nextInt(900));

        String shippingJson;
        String billingJson;
        try {
            shippingJson = objectMapper.writeValueAsString(request.getShippingAddress());
            billingJson = request.getBillingAddress() != null
                    ? objectMapper.writeValueAsString(request.getBillingAddress())
                    : shippingJson;
        } catch (JsonProcessingException e) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Invalid address formatting.");
        }

        // 4. Create Parent Order
        Order parentOrder = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .subtotal(totalSubtotal)
                .discountAmount(totalDiscount)
                .shippingAmount(totalShipping)
                .taxAmount(totalTax)
                .grandTotal(grandTotal)
                .currency("USD")
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .shippingAddressJson(shippingJson)
                .billingAddressJson(billingJson)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        // 5. Partition into Sub-Orders per Seller
        int sellerIndex = 1;
        for (Map.Entry<Seller, List<CartItem>> entry : sellerGroups.entrySet()) {
            Seller seller = entry.getKey();
            List<CartItem> items = entry.getValue();

            BigDecimal sellerSubtotal = BigDecimal.ZERO;
            for (CartItem it : items) {
                sellerSubtotal = sellerSubtotal.add(it.getVariant().getEffectivePrice().multiply(BigDecimal.valueOf(it.getQuantity())));
            }

            BigDecimal commissionRate = seller.getCommissionRateOverride() != null
                    ? seller.getCommissionRateOverride()
                    : BigDecimal.valueOf(10.00);

            BigDecimal commissionAmount = sellerSubtotal.multiply(commissionRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            BigDecimal netSeller = sellerSubtotal.subtract(commissionAmount);

            String subOrderNumber = orderNumber + "-S" + sellerIndex++;

            SellerOrder sellerOrder = SellerOrder.builder()
                    .sellerOrderNumber(subOrderNumber)
                    .seller(seller)
                    .subtotal(sellerSubtotal)
                    .discountAmount(BigDecimal.ZERO)
                    .shippingAmount(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .totalAmount(sellerSubtotal)
                    .commissionRate(commissionRate)
                    .commissionAmount(commissionAmount)
                    .netSellerPayable(netSeller)
                    .status(SellerOrderStatus.PENDING_PAYMENT)
                    .payoutStatus(PayoutStatus.PENDING)
                    .build();

            for (CartItem it : items) {
                ProductVariant variant = it.getVariant();
                Product product = variant.getProduct();
                BigDecimal unitPrice = variant.getEffectivePrice();
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(it.getQuantity()));

                // Atomic stock reservation
                inventoryService.reserveStock(variant.getId(), it.getQuantity(), subOrderNumber);

                OrderItem orderItem = OrderItem.builder()
                        .variant(variant)
                        .product(product)
                        .productTitleSnapshot(product.getTitle())
                        .variantTitleSnapshot(variant.getTitle())
                        .skuSnapshot(variant.getSku())
                        .unitPrice(unitPrice)
                        .quantity(it.getQuantity())
                        .totalPrice(lineTotal)
                        .build();

                sellerOrder.addItem(orderItem);
            }

            parentOrder.addSellerOrder(sellerOrder);
        }

        Order savedOrder = orderRepository.save(parentOrder);

        // Record coupon redemption if applied
        if (couponId != null) {
            couponService.recordRedemption(couponId, customerId, savedOrder.getId(), totalDiscount);
        }

        // Clear cart items
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Multi-seller order placed: [orderId={}, number={}, sellers={}, grandTotal={}]",
                savedOrder.getId(), savedOrder.getOrderNumber(), sellerGroups.size(), savedOrder.getGrandTotal());

        return orderService.getOrderById(savedOrder.getId());
    }
}
