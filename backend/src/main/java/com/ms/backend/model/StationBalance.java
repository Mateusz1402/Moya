package com.ms.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "station_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
}
