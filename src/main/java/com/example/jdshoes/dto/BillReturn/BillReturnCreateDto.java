package com.example.jdshoes.dto.BillReturn;

import lombok.Data;

import java.util.List;

@Data
public class BillReturnCreateDto {
    private Long billId;
    private int percent;
    private String reason;
    private Boolean shipping;
    // Danh sách hàng đổi
    private List<ReturnDto> returnDtos;
    // Danh sách hàng trả
    private List<RefundDto> refundDtos;
}
