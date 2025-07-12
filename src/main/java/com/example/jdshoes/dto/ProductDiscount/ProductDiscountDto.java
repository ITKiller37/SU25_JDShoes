package com.example.jdshoes.dto.ProductDiscount;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Valid
public class ProductDiscountDto {

    private Long id;

    private String code;

    private String name;

    private String type;

    private BigDecimal value;

    private boolean closed;

    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private Long productDetailId;

    private BigDecimal discountedAmount;
}
