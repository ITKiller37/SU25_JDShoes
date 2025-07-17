package com.example.jdshoes.dto.BillReturn;

import com.example.jdshoes.dto.Customer.CustomerDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillReturnDto {

    private Long id;

    private String code;

    private String returnReason;

    private CustomerDto customer;

    private LocalDateTime returnDate;

    private BigDecimal returnMoney;

    private boolean isCancel;

    private int returnStatus;

}
