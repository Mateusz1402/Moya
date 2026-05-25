package com.ms.backend.service;

import com.ms.backend.dto.CreateReceiptItemRequest;
import com.ms.backend.dto.CreateReceiptRequest;
import com.ms.backend.dto.ReceiptDto;
import com.ms.backend.dto.ReceiptItemDto;
import com.ms.backend.model.Pump;
import com.ms.backend.model.Tank;
import com.ms.backend.model.PumpHose;
import com.ms.backend.repository.PumpHoseRepository;
import com.ms.backend.repository.PumpRepository;
import com.ms.backend.repository.ReceiptRepository;
import com.ms.backend.repository.TankRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ms.backend.service.ReceiptItemService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PumpingSimulationService {

    // Flow rate: ~1 litre per second (realistic for a fuel pump ~60L/min)
    private static final double LITRES_PER_SECOND = 1.0;
    private static final String PUMP_STATUS_AVAILABLE = "AVAILABLE";

    private final PumpRepository pumpRepository;
    private final PumpHoseRepository pumpHoseRepository;
    private final TankRepository tankRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemService receiptItemService;
    private final ReceiptService receiptService;

    public PumpingSimulationService(PumpRepository pumpRepository,
                                    PumpHoseRepository pumpHoseRepository,
                                    TankRepository tankRepository,
                                    ReceiptItemService receiptItemService,
                                    ReceiptService receiptService,
                                    ReceiptRepository receiptRepository
                                    ) {
        this.pumpRepository = pumpRepository;
        this.pumpHoseRepository = pumpHoseRepository;
        this.tankRepository = tankRepository;
        this.receiptItemService = receiptItemService;
        this.receiptService = receiptService;
        this.receiptRepository = receiptRepository;
    }


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

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<PumpHose> hoses = pumpHoseRepository.findByPumpId(pump.getId());
        for(PumpHose hose : hoses){
            if(hose.getTank().getId().equals(tank.getId())){
                totalPrice = hose.getPricePerLiter().multiply(litres);
            }
        }

        CreateReceiptRequest receiptRequest = new CreateReceiptRequest(
                LocalDateTime.now(),
                "CARD",
                totalPrice,
                1L
                );

        ReceiptDto receipt = receiptService.createReceipt(receiptRequest);

        CreateReceiptItemRequest request = new CreateReceiptItemRequest(
                receipt.id(),
                null,
                pumpId,
                litres,
                totalPrice.divide(litres),
                totalPrice);

        receiptItemService.createReceiptItem(request);
    }

    public long calculateDurationSeconds(BigDecimal litres) {
        return Math.max(1, Math.round(litres.doubleValue() / LITRES_PER_SECOND));
    }

    private long calculateDurationMs(BigDecimal litres) {
        return calculateDurationSeconds(litres) * 1000;
    }
}
