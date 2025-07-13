package com.example.jdshoes.dto.ProductDiscount;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountedProductDto {

    private Long productDetailId;

    private String productName;

    private String sizeName;

    private String colorName;

    private String barcode;

    private BigDecimal originalPrice;

    private BigDecimal discountedPrice;

    private String discountType; // % hoặc VND

    private BigDecimal discountValue; // giá trị giảm

    private String discountCode;

    private String discountName;

    private Integer quantity;

}
