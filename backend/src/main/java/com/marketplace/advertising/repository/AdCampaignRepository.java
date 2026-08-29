package com.marketplace.advertising.repository;

import com.marketplace.advertising.domain.AdCampaign;
import com.marketplace.advertising.domain.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdCampaignRepository extends JpaRepository<AdCampaign, UUID> {
    Page<AdCampaign> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);
    List<AdCampaign> findByStatus(CampaignStatus status);
}
