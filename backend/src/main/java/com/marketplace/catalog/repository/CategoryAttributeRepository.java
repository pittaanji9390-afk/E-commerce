package com.marketplace.catalog.repository;

import com.marketplace.catalog.domain.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, UUID> {

    List<CategoryAttribute> findByCategoryId(Long categoryId);
}
