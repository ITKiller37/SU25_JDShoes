package com.example.jdshoes.dto.BillReturn;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RefundDto {
    private Long billDetailId;

    //Giá trả lại
    private BigDecimal detailPrice;

    //Số lượng khách hàng trả lại
    private Integer quantityRefund;
}
