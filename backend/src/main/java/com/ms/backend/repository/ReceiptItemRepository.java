package com.ms.backend.repository;

import com.ms.backend.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    public ReceiptItem findAllByReceipt(Long id);
    public ReceiptItem findAllByProduct(Long id);
    public ReceiptItem findAllByPump(Long id);
}
