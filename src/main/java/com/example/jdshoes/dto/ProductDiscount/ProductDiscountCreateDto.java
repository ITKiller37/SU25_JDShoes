package com.example.jdshoes.dto.ProductDiscount;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;
@Data
@Valid
public class ProductDiscountCreateDto {
    List<ProductDiscountDto> productDiscounts;
}
