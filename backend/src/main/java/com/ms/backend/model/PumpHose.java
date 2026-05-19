package com.ms.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pump_hoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PumpHose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_id", nullable = false)
    private Pump pump;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tank_id", nullable = false)
    private Tank tank;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerLiter;

}
