package com.example.jdshoes.dto.Discount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class DiscountDto {

    private Integer id;
    private String code;
    private String name;
    private Integer type;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    private BigDecimal discountAmount;
    private Integer percentage;
    private BigDecimal maximumAmount;
    private BigDecimal minimumAmount;
    private Integer maximumUsage;
    private String note;
    private boolean deleteFlag;
    private Integer status;
}
