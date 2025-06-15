package com.example.jdshoes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DiscountDto {
    private Long id;

    private String code;

    private String name;

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal discountAmount;

    private BigDecimal maximumAmount;

    private BigDecimal minimumAmount;

    private Integer maximumUsage;

    private String status;
}
