package com.ms.backend.repository;

import com.ms.backend.model.SupplyOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyOrderItemRepository extends JpaRepository<SupplyOrderItem, Long> {
}
