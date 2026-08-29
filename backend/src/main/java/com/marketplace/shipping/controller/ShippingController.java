package com.marketplace.shipping.controller;

import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import com.marketplace.shipping.dto.AddTrackingEventRequest;
import com.marketplace.shipping.dto.CreateShipmentRequest;
import com.marketplace.shipping.dto.ShipmentDto;
import com.marketplace.shipping.service.ShippingService;
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

@Tag(name = "Shipping & Fulfillment", description = "Endpoints for vendor package fulfillment and tracking updates")
@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @Operation(summary = "Create shipment and print dispatch label (Seller)")
    @PostMapping("/shipments")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<ShipmentDto>> createShipment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateShipmentRequest request) {
        ShipmentDto shipment = shippingService.createShipment(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(shipment, "Shipment dispatched."));
    }

    @Operation(summary = "Append carrier checkpoint or GPS tracking event")
    @PostMapping("/shipments/{shipmentId}/events")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<ShipmentDto>> addTrackingEvent(
            @PathVariable UUID shipmentId,
            @Valid @RequestBody AddTrackingEventRequest request) {
        ShipmentDto updated = shippingService.addTrackingEvent(shipmentId, request);
        return ResponseEntity.ok(Result.ok(updated, "Tracking checkpoint updated."));
    }

    @Operation(summary = "Public tracking query by carrier tracking number")
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<Result<ShipmentDto>> trackPackage(@PathVariable String trackingNumber) {
        ShipmentDto shipment = shippingService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(Result.ok(shipment));
    }
}
