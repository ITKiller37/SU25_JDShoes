package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.*;

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

    public ProductDiscountDetail( ProductDiscount productDiscount, ProductDetail productDetail) {
        this.productDiscount = productDiscount;
        this.productDetail = productDetail;
    }
}
