package com.ms.backend.service;

import com.ms.backend.model.*;
import com.ms.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class TransactionService {
    private final ReceiptRepository receiptRepository;
    private final TankRepository tankRepository;
    private final ProductRepository productRepository;
    private final PumpHoseRepository pumpHoseRepository;

    public TransactionService(ReceiptRepository receiptRepository, TankRepository tankRepository,
                              ProductRepository productRepository, PumpHoseRepository pumpHoseRepository){
        this.receiptRepository = receiptRepository;
        this.tankRepository = tankRepository;
        this.productRepository = productRepository;
        this.pumpHoseRepository = pumpHoseRepository;
    }

    // TODO: Implement sale processing once ReceiptItem relationship is added to Receipt
    @Transactional
    public Receipt processSale(Receipt receipt){
        return receiptRepository.save(receipt);
    }
}
