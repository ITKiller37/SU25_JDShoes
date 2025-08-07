package com.example.jdshoes.dto.ProductDiscount;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Valid
public class CreateProductDiscountRequest {
    private String code;

    private String name;

    private BigDecimal value;

    private String type; // percentage/fixed

    private String status;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private String description;

    private List<Long> productDetailIds;

    private List<ProductDiscountDetailDto> productDiscounts;

    private BigDecimal originalValue;

    private BigDecimal discountedAmount;

}
