package com.ms.backend.repository;

import com.ms.backend.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
}
