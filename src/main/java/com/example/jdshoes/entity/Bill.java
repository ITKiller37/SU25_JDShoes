package com.example.jdshoes.entity;

import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String code;
    private BigDecimal promotionPrice;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BillStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type")
    private InvoiceType invoiceType;

    @Nationalized
    private String billingAddress;

    private BigDecimal amount;

    private Boolean returnStatus;

    @Nationalized
    private String customerName;

    private String customerEmail;

    private String customerPhoneNumber;

    @Nationalized
    private String customerAddress;

    private BigDecimal shippingFee;

    private Boolean isShipping;

    @Nationalized
    private String shippingNote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "paymentMethodId")
    private PaymentMethod paymentMethod;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bill")
    private List<BillDetail> billDetail;

    @ManyToOne
    @JoinColumn(name = "discountId")
    private Discount discount;
}
