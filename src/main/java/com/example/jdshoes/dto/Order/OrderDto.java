package com.example.jdshoes.dto.Order;

import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDto {
    private Long billId;
    private CustomerDto customer;
    private InvoiceType invoiceType;
    private BillStatus billStatus;
    private Long paymentMethodId;
    private String billingAddress;
    private BigDecimal promotionPrice;
    private Integer voucherId;
    private String orderId;
    private List<OrderDetailDto> orderDetailDtos;
    private Boolean isShipping;
    private Long shippingAddressId; // ID của AddressShipping nếu chọn
    private String shippingFullAddress; // Địa chỉ đầy đủ khi nhập mới
    private BigDecimal shippingFee;
    private String shippingName;
    private String shippingPhone;
    private String shippingNote;
}
