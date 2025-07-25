package com.example.jdshoes.entity;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.ReturnDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "bill_return")
public class BillReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Nationalized
    private String returnReason;

    private LocalDateTime returnDate;

    private Integer percentFeeExchange;
    private BigDecimal returnMoney;

    private boolean isCancel;

    // 0:Chờ xác nhận 1: Chờ giao hàng 2: Đang giao hàng 3: Hoàn thành 4: Hủy
    private int returnStatus;

    @OneToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @OneToMany(mappedBy = "billReturn", cascade = CascadeType.ALL)
    private List<ReturnDetail> returnDetails;

    // có cái này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billReturnAccountId")
    private Account account;

}

