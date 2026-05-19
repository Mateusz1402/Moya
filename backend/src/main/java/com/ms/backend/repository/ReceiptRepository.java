package com.ms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.backend.model.Receipt;
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
