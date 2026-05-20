package com.ms.backend.repository;

import com.ms.backend.model.StationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationBalanceRepository extends JpaRepository<StationBalance, Long> {

}
