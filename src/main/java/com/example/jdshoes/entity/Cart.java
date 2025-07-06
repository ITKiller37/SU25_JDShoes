package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;

    private int quantity;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}