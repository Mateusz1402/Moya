package com.ms.backend.service;

import com.ms.backend.dto.CreateReceiptRequest;
import com.ms.backend.dto.ReceiptDto;
import com.ms.backend.model.Receipt;
import com.ms.backend.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository){
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public ReceiptDto createReceipt(CreateReceiptRequest request){
        Receipt receipt = Receipt.builder()
                .transactionDate(request.transactionDate())
                .paymentMethod(request.paymentMethod())
                .totalAmount(request.totalAmount())
                .cashierId(request.cashierId())
                .build();

        return toReceiptDto(receiptRepository.save(receipt));
    }

    private ReceiptDto toReceiptDto(Receipt receipt){
        return new ReceiptDto(receipt.getId(),
                receipt.getTransactionDate(),
                receipt.getPaymentMethod(),
                receipt.getTotalAmount(),
                receipt.getCashierId());
    }


}
