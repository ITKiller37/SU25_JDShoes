package com.example.jdshoes.dto.Bill;

import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BillDto {

    private Long id;
    private String code;
    private BigDecimal promotionPrice;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private BillStatus status;
    private InvoiceType invoiceType;
    private String billingAddress;
    private BigDecimal shippingFee;
    private BigDecimal amount;
    private Boolean returnStatus;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerAddress;
    private CustomerDto customer;

    private BigDecimal totalAmount;


}
