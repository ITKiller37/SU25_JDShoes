package com.example.jdshoes.dto.ProductDiscount;

import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Valid
public class ProductDiscountDetailDto {
    private Long productDetailId;
    private BigDecimal discountedAmount;
}
