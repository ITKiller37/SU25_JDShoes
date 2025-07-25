package com.example.jdshoes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "OrderTemp")
public class OrderTemp {

    @Id
    private String orderId;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String orderData;

    @Column(name = "vnp_txn_ref")
    private String vnpTxnRef;

    private BigDecimal shippingFee;

    private LocalDateTime createdAt;
}
