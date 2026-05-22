package com.ms.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pumps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pump {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pump_status", nullable = false)
    private String pumpStatus;
}
