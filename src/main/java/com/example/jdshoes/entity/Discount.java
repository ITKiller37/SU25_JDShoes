package com.example.jdshoes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Size(max = 50)
    @NotNull
    @Column(name = "Code", nullable = false, length = 50)
    private String code;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Size(max = 20)
    @NotNull
    @Column(name = "Type", nullable = false, length = 20)
    private String type;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "DiscountAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "MaximumAmount", precision = 10, scale = 2)
    private BigDecimal maximumAmount;

    @Column(name = "MinimumAmount", precision = 10, scale = 2)
    private BigDecimal minimumAmount;

    @Column(name = "MaximumUsage")
    private Integer maximumUsage;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "Status", nullable = false, length = 50)
    private String status;

}