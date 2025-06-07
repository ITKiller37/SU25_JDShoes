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

    @NotBlank(message = "không được để trống")
    private String code;

    @NotBlank(message = "không được để trống")
    private String name;

    @NotBlank(message = "không được để trống")
    private String type;

    @NotNull(message = "không được để trống")
    private LocalDate startDate;

    @NotNull(message = "không được để trống")
    private LocalDate endDate;

    @NotNull(message = "không được để trống")
    private BigDecimal discountAmount;

    @NotNull(message = "không được để trống")
    private BigDecimal maximumAmount;

    @NotNull(message = "không được để trống")
    private BigDecimal minimumAmount;

    @NotNull(message = "không được để trống")
    private Integer maximumUsage;

    @NotBlank(message = "không được để trống")
    private String status;
}
