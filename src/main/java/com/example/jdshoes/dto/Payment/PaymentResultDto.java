package com.example.jdshoes.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResultDto {
    private String txnRef;
    private String amount;
    private String responseCode;
    private String bankCode;
    private String datePay;
    private String transactionStatus;
}
