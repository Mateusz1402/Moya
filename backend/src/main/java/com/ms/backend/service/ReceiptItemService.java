package com.ms.backend.service;

import com.ms.backend.dto.CreateReceiptItemRequest;
import com.ms.backend.dto.CreateReceiptRequest;
import com.ms.backend.service.ReceiptService;
import com.ms.backend.dto.ReceiptItemDto;
import com.ms.backend.exception.ResourceNotFoundException;
import com.ms.backend.model.Product;
import com.ms.backend.model.Pump;
import com.ms.backend.model.Receipt;
import com.ms.backend.model.ReceiptItem;
import com.ms.backend.repository.ProductRepository;
import com.ms.backend.repository.PumpRepository;
import com.ms.backend.repository.ReceiptItemRepository;
import com.ms.backend.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceiptItemService {
    private final ReceiptItemRepository receiptItemRepository;
    private final ReceiptRepository receiptRepository;
    private final ProductRepository productRepository;
    private final PumpRepository pumpRepository;

    public ReceiptItemService(ReceiptItemRepository receiptItemRepository,
                                 ReceiptRepository receiptRepository,
                                 ProductRepository productRepository,
                                 PumpRepository pumpRepository,
                              ReceiptService receiptService){
        this.receiptItemRepository = receiptItemRepository;
        this.receiptRepository = receiptRepository;
        this.productRepository = productRepository;
        this.pumpRepository = pumpRepository;
    }

    public List<ReceiptItemDto> getAllReceiptItem(){
        return receiptItemRepository.findAll().stream().map(this::toReceiptItemDto).toList();
    }

    public ReceiptItemDto getReceiptItemById(Long id){
        ReceiptItem receiptItem = receiptItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find such id of receipt item" + id));
        return toReceiptItemDto(receiptItem);
    }

    public ReceiptItemDto getReceiptItemByReceiptId(Long id){
        ReceiptItem receiptItem = receiptItemRepository.findAllByReceipt(id);
        return toReceiptItemDto(receiptItem);
    }

    public ReceiptItemDto getReceiptItemByProductId(Long id){
        ReceiptItem receiptItem = receiptItemRepository.findAllByProduct(id);
        return toReceiptItemDto(receiptItem);
    }

    public ReceiptItemDto getReceiptItemByPumpId(Long id){
        ReceiptItem receiptItem = receiptItemRepository.findAllByPump(id);
        return toReceiptItemDto(receiptItem);
    }

    @Transactional
    public ReceiptItemDto createReceiptItem(CreateReceiptItemRequest request){

        Receipt receipt= receiptRepository.findById(request.receiptId())
                .orElseThrow(() -> new ResourceNotFoundException("can not find receipt of such id: " + request.receiptId()));

        Product product = null;
        if (request.productId() != null) {
            product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Can not find product of such id: " + request.productId()));
        }

        Pump pump = null;
        if (request.pumpId() != null) {
            pump = pumpRepository.findById(request.pumpId())
                    .orElseThrow(() -> new ResourceNotFoundException("Can not find pump of such id: " + request.pumpId()));
        }

        ReceiptItem receiptItem = ReceiptItem.builder()
                .receipt(receipt)
                .product(product)
                .pump(pump)
                .quantity(request.quantity())
                .unitPrice(request.unitPrice())
                .totalPrice(request.totalPrice())
                .build();


        return toReceiptItemDto(receiptItemRepository.save(receiptItem));
    }

    public void deleteReceiptItem(Long id){
        if(!receiptItemRepository.existsById(id)){
            throw new ResourceNotFoundException("Can not find receipt item of such id " + id);
        }
        receiptItemRepository.deleteById(id);
    }


    private ReceiptItemDto toReceiptItemDto(ReceiptItem receiptItem){
        return new ReceiptItemDto(receiptItem.getId(),
                                    receiptItem.getReceipt().getId(),
                                    receiptItem.getProduct() != null ? receiptItem.getProduct().getId() : null,
                                    receiptItem.getPump() != null ? receiptItem.getPump().getId() : null,
                                    receiptItem.getQuantity(),
                                    receiptItem.getUnitPrice(),
                                    receiptItem.getTotalPrice());
    }


}
