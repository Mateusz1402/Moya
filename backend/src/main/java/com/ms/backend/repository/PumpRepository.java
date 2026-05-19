package com.ms.backend.repository;
import com.ms.backend.model.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PumpRepository extends JpaRepository<Pump, Long> {
}
