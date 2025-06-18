package com.example.jdshoes.dto.Discount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiscountDto {
    private Integer id;

    private String code;

    private String name;

    private Boolean type;

    private LocalDate startDate;

    private LocalDateTime endDate;

    private BigDecimal discountAmount;

    private BigDecimal maximumAmount;

    private BigDecimal minimumAmount;

    private Integer maximumUsage;

    private Boolean status;
}
