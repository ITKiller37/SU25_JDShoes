package com.example.jdshoes.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Discount")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;


    @Column(name = "Code", insertable = false, updatable = false)
    private String code;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    private String name;


    @Column(name = "Type")
    private Integer type;

    @Column(name = "StartDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    @Column(name = "DiscountAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "Percentage")
    private Integer percentage;

    @Column(name = "MaximumAmount", precision = 10, scale = 2)
    private BigDecimal maximumAmount;

    @Column(name = "MinimumAmount", precision = 10, scale = 2)
    private BigDecimal minimumAmount;

    @Column(name = "MaximumUsage")
    private Integer maximumUsage;

    @Column(name = "Status")
    private Boolean status;
}
