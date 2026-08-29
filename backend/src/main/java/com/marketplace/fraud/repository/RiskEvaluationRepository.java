package com.marketplace.fraud.repository;

import com.marketplace.fraud.domain.RiskEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskEvaluationRepository extends JpaRepository<RiskEvaluation, UUID> {
    Optional<RiskEvaluation> findByOrderId(UUID orderId);
}
