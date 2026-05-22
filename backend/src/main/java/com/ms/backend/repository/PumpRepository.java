package com.ms.backend.repository;

import com.ms.backend.model.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PumpRepository extends JpaRepository<Pump, Long> {
    List<Pump> findByPumpStatus(String pumpStatus);
}
