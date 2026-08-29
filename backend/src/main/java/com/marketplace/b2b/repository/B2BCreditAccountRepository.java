package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.B2BCreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface B2BCreditAccountRepository extends JpaRepository<B2BCreditAccount, UUID> {
}
