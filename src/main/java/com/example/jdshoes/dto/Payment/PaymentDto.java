package com.example.jdshoes.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDto {
    private String status;
    private String message;
    private String url;
    private String vnp_TxnRef;
}
