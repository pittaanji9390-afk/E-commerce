package com.marketplace.shipping.service;

import com.marketplace.order.domain.PayoutStatus;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.domain.SellerOrderStatus;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.shipping.domain.Shipment;
import com.marketplace.shipping.domain.ShipmentEvent;
import com.marketplace.shipping.domain.ShipmentStatus;
import com.marketplace.shipping.dto.AddTrackingEventRequest;
import com.marketplace.shipping.dto.CreateShipmentRequest;
import com.marketplace.shipping.dto.ShipmentDto;
import com.marketplace.shipping.dto.ShipmentEventDto;
import com.marketplace.shipping.repository.ShipmentEventRepository;
import com.marketplace.shipping.repository.ShipmentRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository eventRepository;
    private final SellerOrderRepository sellerOrderRepository;

    @Transactional
    public ShipmentDto createShipment(UUID sellerId, CreateShipmentRequest request) {
        SellerOrder sellerOrder = sellerOrderRepository.findById(request.getSellerOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", request.getSellerOrderId()));

        if (sellerId != null && !sellerOrder.getSeller().getId().equals(sellerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You can only ship your own vendor sub-orders.");
        }

        Shipment shipment = Shipment.builder()
                .sellerOrder(sellerOrder)
                .carrier(request.getCarrier().toUpperCase())
                .trackingNumber(request.getTrackingNumber().trim())
                .shippingLabelUrl(request.getShippingLabelUrl())
                .status(ShipmentStatus.SHIPPED)
                .shippedAt(Instant.now())
                .build();

        Shipment saved = shipmentRepository.save(shipment);

        // Transition Sub-Order Status to SHIPPED
        sellerOrder.setStatus(SellerOrderStatus.SHIPPED);
        sellerOrderRepository.save(sellerOrder);

        // Initial scan event
        ShipmentEvent initialEvent = ShipmentEvent.builder()
                .shipment(saved)
                .status("SHIPPED")
                .location("Merchant Fulfillment Facility")
                .description("Package scanned and departed origin facility via " + shipment.getCarrier())
                .eventTimestamp(Instant.now())
                .build();
        eventRepository.save(initialEvent);

        log.info("Shipment created: [tracking={}, carrier={}, subOrder={}]",
                saved.getTrackingNumber(), saved.getCarrier(), sellerOrder.getSellerOrderNumber());

        return toDto(saved);
    }

    @Transactional
    public ShipmentDto addTrackingEvent(UUID shipmentId, AddTrackingEventRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        ShipmentEvent event = ShipmentEvent.builder()
                .shipment(shipment)
                .status(request.getStatus().toUpperCase())
                .location(request.getLocation())
                .description(request.getDescription())
                .eventTimestamp(Instant.now())
                .build();
        eventRepository.save(event);

        if ("DELIVERED".equalsIgnoreCase(request.getStatus())) {
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipment.setDeliveredAt(Instant.now());
            shipmentRepository.save(shipment);

            SellerOrder sellerOrder = shipment.getSellerOrder();
            sellerOrder.setStatus(SellerOrderStatus.DELIVERED);
            sellerOrder.setPayoutStatus(PayoutStatus.ELIGIBLE); // Ready for escrow release
            sellerOrderRepository.save(sellerOrder);
        }

        return toDto(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentDto getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingNumber", trackingNumber));
        return toDto(shipment);
    }

    private ShipmentDto toDto(Shipment s) {
        List<ShipmentEvent> events = eventRepository.findByShipmentIdOrderByEventTimestampDesc(s.getId());
        List<ShipmentEventDto> eventDtos = events.stream()
                .map(e -> ShipmentEventDto.builder()
                        .id(e.getId())
                        .status(e.getStatus())
                        .location(e.getLocation())
                        .description(e.getDescription())
                        .eventTimestamp(e.getEventTimestamp())
                        .build())
                .collect(Collectors.toList());

        return ShipmentDto.builder()
                .id(s.getId())
                .sellerOrderId(s.getSellerOrder().getId())
                .carrier(s.getCarrier())
                .trackingNumber(s.getTrackingNumber())
                .shippingLabelUrl(s.getShippingLabelUrl())
                .status(s.getStatus())
                .shippedAt(s.getShippedAt())
                .estimatedDelivery(s.getEstimatedDelivery())
                .deliveredAt(s.getDeliveredAt())
                .events(eventDtos)
                .build();
    }
}
