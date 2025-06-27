package com.example.jdshoes.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
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


    @Column(name = "Code", unique = true)
    private String code;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    private String name;


    @Column(name = "Type")
    private Integer type;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "DiscountAmount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "Percentage")
    private Integer percentage;

    @Column(name = "MaximumAmount", precision = 10, scale = 2)
    private BigDecimal maximumAmount;

    @Column(name = "MinimumAmount", precision = 10, scale = 2)
    private BigDecimal minimumAmount;

    @Column(name = "MaximumUsage")
    private Integer maximumUsage;

    @Nationalized
    @Column(name = "Note")
    private String note;

    @Column(name = "deleteFlag")
    private Boolean deleteFlag;

    @Column(name = "Status")
    private Integer status;
}
