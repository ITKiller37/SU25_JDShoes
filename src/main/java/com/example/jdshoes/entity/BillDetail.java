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
@Table(name = "BillDetail")
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;

    private BigDecimal detailPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billId")
    private Bill bill;

    private Integer exchangeQuantity;
}
