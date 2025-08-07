package com.example.jdshoes.entity;

import com.example.jdshoes.entity.enumClass.BillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "BillHistory")
public class BillHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "billId", nullable = false)
    private Bill bill;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Column(name = "note")
    @Nationalized
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy; // Liên kết với nhân viên (Account)
}
