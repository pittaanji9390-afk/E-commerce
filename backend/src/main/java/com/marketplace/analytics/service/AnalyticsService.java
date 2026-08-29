package com.marketplace.analytics.service;

import com.marketplace.analytics.dto.AdminPlatformAnalyticsDto;
import com.marketplace.analytics.dto.SellerDashboardAnalyticsDto;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.order.domain.Order;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.seller.domain.SellerStatus;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.seller.repository.SellerVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SellerVerificationRepository verificationRepository;

    @Transactional(readOnly = true)
    public SellerDashboardAnalyticsDto getSellerAnalytics(UUID sellerId) {
        List<SellerOrder> orders = sellerOrderRepository.findBySellerId(sellerId, Pageable.unpaged()).getContent();

        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal commissionPaid = BigDecimal.ZERO;
        BigDecimal netEarnings = BigDecimal.ZERO;
        long totalItems = 0;

        for (SellerOrder so : orders) {
            grossRevenue = grossRevenue.add(so.getTotalAmount());
            commissionPaid = commissionPaid.add(so.getCommissionAmount());
            netEarnings = netEarnings.add(so.getNetSellerPayable());
            totalItems += so.getItems().size();
        }

        BigDecimal aov = orders.isEmpty() ? BigDecimal.ZERO :
                grossRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_EVEN);

        long activeProducts = productRepository.findBySellerId(sellerId, Pageable.unpaged())
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE).getTotalElements();

        Map<String, BigDecimal> revenueTrends = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 29; i >= 0; i--) {
            revenueTrends.put(now.minusDays(i).format(dtf), BigDecimal.ZERO);
        }

        return SellerDashboardAnalyticsDto.builder()
                .sellerId(sellerId)
                .totalGrossRevenue(grossRevenue)
                .totalCommissionPaid(commissionPaid)
                .netEarnings(netEarnings)
                .totalOrders(orders.size())
                .totalItemsSold(totalItems)
                .activeProductsCount(activeProducts)
                .lowStockCount(0)
                .averageOrderValue(aov)
                .revenueLast30Days(revenueTrends)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminPlatformAnalyticsDto getPlatformAnalytics() {
        List<Order> orders = orderRepository.findAll();

        BigDecimal platformGmv = BigDecimal.ZERO;
        BigDecimal totalTakeRate = BigDecimal.ZERO;

        for (Order o : orders) {
            platformGmv = platformGmv.add(o.getGrandTotal());
            for (SellerOrder so : o.getSellerOrders()) {
                totalTakeRate = totalTakeRate.add(so.getCommissionAmount());
            }
        }

        long totalUsers = userRepository.count();
        long totalSellers = sellerRepository.count();
        long pendingKyc = sellerRepository.findByStatus(SellerStatus.UNDER_REVIEW).size();
        long totalProducts = productRepository.count();

        Map<String, BigDecimal> gmvTrends = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 29; i >= 0; i--) {
            gmvTrends.put(now.minusDays(i).format(dtf), BigDecimal.ZERO);
        }

        return AdminPlatformAnalyticsDto.builder()
                .platformGmv(platformGmv)
                .totalTakeRateRevenue(totalTakeRate)
                .totalUsers(totalUsers)
                .totalSellers(totalSellers)
                .pendingKycCount(pendingKyc)
                .totalOrders(orders.size())
                .totalProducts(totalProducts)
                .gmvLast30Days(gmvTrends)
                .build();
    }
}
