package com.ms.backend.service;

import com.ms.backend.dto.*;
import com.ms.backend.exception.BusinessLogicException;
import com.ms.backend.exception.ResourceNotFoundException;
import com.ms.backend.model.Pump;
import com.ms.backend.model.PumpHose;
import com.ms.backend.model.Tank;
import com.ms.backend.repository.PumpHoseRepository;
import com.ms.backend.repository.PumpRepository;
import com.ms.backend.repository.TankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FuelService {

    private static final String PUMP_STATUS_AVAILABLE = "AVAILABLE";
    private static final String PUMP_STATUS_IN_USE = "IN_USE";
    private static final String PUMP_STATUS_OUT_OF_SERVICE = "OUT_OF_SERVICE";

    private final TankRepository tankRepository;
    private final PumpRepository pumpRepository;
    private final PumpHoseRepository pumpHoseRepository;
    private final PumpingSimulationService pumpingSimulationService;

    public FuelService(TankRepository tankRepository, PumpRepository pumpRepository,
                       PumpHoseRepository pumpHoseRepository, PumpingSimulationService pumpingSimulationService) {
        this.tankRepository = tankRepository;
        this.pumpRepository = pumpRepository;
        this.pumpHoseRepository = pumpHoseRepository;
        this.pumpingSimulationService = pumpingSimulationService;
    }

    // ─── Tank operations ───────────────────────────────────────────────

    public List<TankDto> getAllTanks() {
        return tankRepository.findAll().stream()
                .map(this::toTankDto)
                .toList();
    }

    public TankDto getTankById(Long id) {
        Tank tank = tankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tank not found with id: " + id));
        return toTankDto(tank);
    }

    @Transactional
    public TankDto createTank(CreateTankRequest request) {
        Tank tank = Tank.builder()
                .fuelType(request.fuelType())
                .capacityLitres(request.capacityLitres())
                .currentLevelLitres(request.currentLevelLitres())
                .build();
        return toTankDto(tankRepository.save(tank));
    }

    @Transactional
    public TankDto updateTank(Long id, CreateTankRequest request) {
        Tank tank = tankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tank not found with id: " + id));
        tank.setFuelType(request.fuelType());
        tank.setCapacityLitres(request.capacityLitres());
        tank.setCurrentLevelLitres(request.currentLevelLitres());
        return toTankDto(tankRepository.save(tank));
    }

    public void deleteTank(Long id) {
        if (!tankRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tank not found with id: " + id);
        }
        tankRepository.deleteById(id);
    }

    // ─── Pump operations ───────────────────────────────────────────────

    public List<PumpDto> getAllPumps() {
        return pumpRepository.findAll().stream()
                .map(this::toPumpDto)
                .toList();
    }

    public PumpDto getPumpById(Long id) {
        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pump not found with id: " + id));
        return toPumpDto(pump);
    }

    @Transactional
    public PumpDto createPump(CreatePumpRequest request) {
        Pump pump = Pump.builder()
                .pumpStatus(request.pumpStatus() != null ? request.pumpStatus() : PUMP_STATUS_AVAILABLE)
                .build();
        return toPumpDto(pumpRepository.save(pump));
    }

    @Transactional
    public PumpDto updatePumpStatus(Long id, String status) {
        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pump not found with id: " + id));
        validatePumpStatus(status);
        pump.setPumpStatus(status);
        return toPumpDto(pumpRepository.save(pump));
    }

    public void deletePump(Long id) {
        if (!pumpRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pump not found with id: " + id);
        }
        pumpRepository.deleteById(id);
    }

    // ─── PumpHose operations ───────────────────────────────────────────

    @Transactional
    public PumpHoseDto createPumpHose(CreatePumpHoseRequest request) {
        Pump pump = pumpRepository.findById(request.pumpId())
                .orElseThrow(() -> new ResourceNotFoundException("Pump not found with id: " + request.pumpId()));
        Tank tank = tankRepository.findById(request.tankId())
                .orElseThrow(() -> new ResourceNotFoundException("Tank not found with id: " + request.tankId()));

        PumpHose hose = PumpHose.builder()
                .pump(pump)
                .tank(tank)
                .fuelType(request.fuelType())
                .pricePerLiter(request.pricePerLiter())
                .build();
        return toPumpHoseDto(pumpHoseRepository.save(hose));
    }

    public List<PumpHoseDto> getHosesByPump(Long pumpId) {
        return pumpHoseRepository.findByPumpId(pumpId).stream()
                .map(this::toPumpHoseDto)
                .toList();
    }

    public void deletePumpHose(Long id) {
        if (!pumpHoseRepository.existsById(id)) {
            throw new ResourceNotFoundException("PumpHose not found with id: " + id);
        }
        pumpHoseRepository.deleteById(id);
    }

    // ─── Fuel dispensing (simulation core) ─────────────────────────────

    @Transactional
    public DispenseFuelResponse dispenseFuel(DispenseFuelRequest request) {
        Pump pump = pumpRepository.findById(request.pumpId())
                .orElseThrow(() -> new ResourceNotFoundException("Pump not found with id: " + request.pumpId()));

        if (!PUMP_STATUS_AVAILABLE.equals(pump.getPumpStatus())) {
            throw new BusinessLogicException("Pump " + pump.getId() + " is not available (status: " + pump.getPumpStatus() + ")");
        }

        PumpHose hose = pumpHoseRepository.findById(request.hoseId())
                .orElseThrow(() -> new ResourceNotFoundException("Hose not found with id: " + request.hoseId()));

        if (!hose.getPump().getId().equals(pump.getId())) {
            throw new BusinessLogicException("Hose " + hose.getId() + " does not belong to pump " + pump.getId());
        }

        Tank tank = hose.getTank();

        if (tank.getCurrentLevelLitres().compareTo(request.litres()) < 0) {
            throw new BusinessLogicException(
                    "Insufficient fuel in tank. Available: " + tank.getCurrentLevelLitres() + "L, requested: " + request.litres() + "L");
        }

        // Mark pump as in use immediately
        pump.setPumpStatus(PUMP_STATUS_IN_USE);
        pumpRepository.save(pump);

        // Calculate cost
        BigDecimal estimatedCost = request.litres().multiply(hose.getPricePerLiter()).setScale(2, RoundingMode.HALF_UP);
        long durationSeconds = pumpingSimulationService.calculateDurationSeconds(request.litres());

        // Kick off async pumping simulation (decreases tank + sets pump back to AVAILABLE after delay)
        pumpingSimulationService.simulatePumping(pump.getId(), tank.getId(), request.litres());

        return new DispenseFuelResponse(
                pump.getId(),
                hose.getId(),
                hose.getFuelType(),
                request.litres(),
                hose.getPricePerLiter(),
                estimatedCost,
                durationSeconds,
                "PUMPING"
        );
    }

    // ─── Mapping helpers ───────────────────────────────────────────────

    private TankDto toTankDto(Tank tank) {
        BigDecimal fillPercentage = tank.getCurrentLevelLitres()
                .divide(tank.getCapacityLitres(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);

        return new TankDto(
                tank.getId(),
                tank.getFuelType(),
                tank.getCapacityLitres(),
                tank.getCurrentLevelLitres(),
                fillPercentage
        );
    }

    private PumpDto toPumpDto(Pump pump) {
        List<PumpHoseDto> hoses = pumpHoseRepository.findByPumpId(pump.getId()).stream()
                .map(this::toPumpHoseDto)
                .toList();
        return new PumpDto(pump.getId(), pump.getPumpStatus(), hoses);
    }

    private PumpHoseDto toPumpHoseDto(PumpHose hose) {
        return new PumpHoseDto(
                hose.getId(),
                hose.getPump().getId(),
                hose.getTank().getId(),
                hose.getFuelType(),
                hose.getPricePerLiter()
        );
    }

    private void validatePumpStatus(String status) {
        if (!PUMP_STATUS_AVAILABLE.equals(status)
                && !PUMP_STATUS_IN_USE.equals(status)
                && !PUMP_STATUS_OUT_OF_SERVICE.equals(status)) {
            throw new BusinessLogicException(
                    "Invalid pump status: " + status + ". Must be one of: AVAILABLE, IN_USE, OUT_OF_SERVICE");
        }
    }
}
