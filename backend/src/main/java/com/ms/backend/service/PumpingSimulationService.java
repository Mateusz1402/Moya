package com.ms.backend.service;

import com.ms.backend.model.Pump;
import com.ms.backend.model.Tank;
import com.ms.backend.repository.PumpRepository;
import com.ms.backend.repository.TankRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PumpingSimulationService {

    // Flow rate: ~1 litre per second (realistic for a fuel pump ~60L/min)
    private static final double LITRES_PER_SECOND = 1.0;
    private static final String PUMP_STATUS_AVAILABLE = "AVAILABLE";

    private final PumpRepository pumpRepository;
    private final TankRepository tankRepository;

    public PumpingSimulationService(PumpRepository pumpRepository, TankRepository tankRepository) {
        this.pumpRepository = pumpRepository;
        this.tankRepository = tankRepository;
    }

    /**
     * Simulates the fuel pumping process asynchronously.
     * Waits for the calculated duration, then decreases tank level and sets pump back to AVAILABLE.
     */
    @Async
    public void simulatePumping(Long pumpId, Long tankId, BigDecimal litres) {
        long durationMs = calculateDurationMs(litres);

        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        finishPumping(pumpId, tankId, litres);
    }

    @Transactional
    protected void finishPumping(Long pumpId, Long tankId, BigDecimal litres) {
        Tank tank = tankRepository.findById(tankId).orElse(null);
        if (tank != null) {
            BigDecimal newLevel = tank.getCurrentLevelLitres().subtract(litres);
            if (newLevel.compareTo(BigDecimal.ZERO) < 0) {
                newLevel = BigDecimal.ZERO;
            }
            tank.setCurrentLevelLitres(newLevel);
            tankRepository.save(tank);
        }

        Pump pump = pumpRepository.findById(pumpId).orElse(null);
        if (pump != null) {
            pump.setPumpStatus(PUMP_STATUS_AVAILABLE);
            pumpRepository.save(pump);
        }
    }

    public long calculateDurationSeconds(BigDecimal litres) {
        return Math.max(1, Math.round(litres.doubleValue() / LITRES_PER_SECOND));
    }

    private long calculateDurationMs(BigDecimal litres) {
        return calculateDurationSeconds(litres) * 1000;
    }
}
