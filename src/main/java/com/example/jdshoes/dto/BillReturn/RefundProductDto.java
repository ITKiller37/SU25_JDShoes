package com.example.jdshoes.dto.BillReturn;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundProductDto {
    private String productName;
    private Long productDetailId;
    private String size;
    private String color;

    //Giá trả lại
    private BigDecimal detailPrice;

    //Số lượng khách hàng trả lại
    private Integer quantityRefund;
}
