package com.ms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ms.backend.model.PumpHose;

import java.util.List;

@Repository
public interface PumpHoseRepository extends JpaRepository<PumpHose, Long> {
    List<PumpHose> findByPumpId(Long pumpId);
}
