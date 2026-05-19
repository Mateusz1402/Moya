package com.ms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tanks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @Column(name = "capacity_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacityLitres;

    @Column(name = "current_level_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentLevelLitres;
}
