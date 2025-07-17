package com.example.jdshoes.dto.BillReturn;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnProductDto {
    private String productName;
    private Long productDetailId;
    private String size;
    private String color;

    //Giá đổi
    private BigDecimal detailPrice;

    //Số lượng đổi
    private Integer quantityReturn;
}
