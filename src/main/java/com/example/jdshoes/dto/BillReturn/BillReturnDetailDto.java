package com.example.jdshoes.dto.BillReturn;

import com.example.jdshoes.dto.Customer.CustomerDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillReturnDetailDto {
    private Long id;
    private String billCode;
    private Long billId;
    private LocalDateTime returnDate;
    private CustomerDto customerDto;
    private String billReturnCode;
    private Integer percentFeeExchange;
    private BigDecimal returnMoney;
    private int billReturnStatus;
    // Danh sách hàng đổi
    private List<ReturnProductDto> returnProductDtos;
    // Danh sách hàng trả
    private List<RefundProductDto> refundProductDtos;

    private String handledByName; // hoặc dùng handledById nếu cần
    private String handledByCode;

}
