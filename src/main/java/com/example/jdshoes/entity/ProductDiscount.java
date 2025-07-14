package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ProductDiscount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Nationalized
    private String name;
    private String type;
    private BigDecimal value;
    private boolean closed;

    @Nationalized
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal discountedAmount;

    @Nationalized
    private String status;

    @OneToMany(mappedBy = "productDiscount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDiscountDetail> productDiscountDetails = new ArrayList<>();

}

