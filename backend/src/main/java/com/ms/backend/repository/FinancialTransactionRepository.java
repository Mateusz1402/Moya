package com.ms.backend.repository;

import com.ms.backend.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ms.backend.model.FinancialTransaction;
@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
}
