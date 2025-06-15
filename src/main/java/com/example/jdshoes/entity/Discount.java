package com.example.jdshoes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Column(name = "Code", nullable = false, length = 50)
    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    @NotBlank(message = "Tên giảm giá không được để trống")
    private String name;

    @Size(max = 20)
    @NotNull
    @Column(name = "Type", nullable = false, length = 20)
    private String type;

    @Column(name = "StartDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày bắt đầu giảm giá không được để trống")
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày kết thúc giảm giá không được để trống")
    private LocalDate endDate;

    @Column(name = "DiscountAmount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "không được để trống")
    private BigDecimal discountAmount;

    @Column(name = "MaximumAmount", precision = 10, scale = 2)
    @NotNull(message = "không được để trống")
    private BigDecimal maximumAmount;

    @Column(name = "MinimumAmount", precision = 10, scale = 2)
    @NotNull(message = "không được để trống")
    private BigDecimal minimumAmount;

    @Column(name = "MaximumUsage")
    @NotNull(message = "không được để trống")
    private Integer maximumUsage;

    @Column(name = "Status")
    private Boolean status;

}