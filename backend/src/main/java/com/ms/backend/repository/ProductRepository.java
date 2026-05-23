package com.ms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ms.backend.model.Product;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findAllByCategory(String category);
    public Product findAllByName(String name);
}
