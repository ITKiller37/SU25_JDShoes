package org.example.su25_jdshoes_ph48766.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ProductDiscount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String type; // percent, amount, free_shipping,...

    private BigDecimal value;

    private String status;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}
