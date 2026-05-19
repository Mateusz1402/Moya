package com.ms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ms.backend.model.Product;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
