package com.marketplace.coupon.repository;

import com.marketplace.coupon.domain.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, UUID> {

    long countByCouponIdAndCustomerId(UUID couponId, UUID customerId);
}
