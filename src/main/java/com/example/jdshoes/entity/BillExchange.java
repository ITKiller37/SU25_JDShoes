package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "BillExchange")
public class BillExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Nationalized
    private String exchangeReason;

    private LocalDateTime exchangeDate;

    private boolean isCancel;

    // 0:Chờ xác nhận 1: Chờ giao hàng 2: Đang giao hàng 3: Hoàn thành 4: Hủy
    private int exchangeStatus;

    @OneToOne
    @JoinColumn(name = "billId")
    private Bill bill;

    @OneToMany(mappedBy = "billExchange", cascade = CascadeType.ALL)
    private List<ExchangeDetail> exchangeDetails;
}
