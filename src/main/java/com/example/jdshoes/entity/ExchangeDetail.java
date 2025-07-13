package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ExchangeDetail")
public class ExchangeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Số lượng đổi
    private Integer quantityExchange;

    //Giá đổi
    private BigDecimal momentPriceRefund;

    @ManyToOne
    @JoinColumn(name = "billExchangeId")
    private BillExchange billExchange;


    @ManyToOne
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;


}
