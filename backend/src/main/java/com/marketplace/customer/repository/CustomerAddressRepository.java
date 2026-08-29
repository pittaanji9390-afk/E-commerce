package com.marketplace.customer.repository;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

    List<CustomerAddress> findByCustomerId(UUID customerId);

    Optional<CustomerAddress> findByIdAndCustomerId(UUID id, UUID customerId);

    @Modifying
    @Query("UPDATE CustomerAddress a SET a.defaultShipping = false WHERE a.customer = :customer")
    void clearDefaultShipping(Customer customer);

    @Modifying
    @Query("UPDATE CustomerAddress a SET a.defaultBilling = false WHERE a.customer = :customer")
    void clearDefaultBilling(Customer customer);
}
