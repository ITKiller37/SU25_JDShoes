package com.example.jdshoes.dto.Bill;

import com.example.jdshoes.dto.CustomerDto.CustomerDto;
import com.example.jdshoes.entity.BillDetail;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.entity.PaymentMethod;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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


}
