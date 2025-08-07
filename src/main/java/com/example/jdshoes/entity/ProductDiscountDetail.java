package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductDiscountDetail")
@Data
@NoArgsConstructor
public class ProductDiscountDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productDiscountId")
    private ProductDiscount productDiscount;

    @ManyToOne
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;

    private BigDecimal discountedAmount;

    public ProductDiscountDetail( ProductDiscount productDiscount, ProductDetail productDetail) {
        this.productDiscount = productDiscount;
        this.productDetail = productDetail;
    }
}
