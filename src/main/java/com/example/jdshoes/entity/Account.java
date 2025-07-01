package com.example.jdshoes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private boolean isNonLocked;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;


    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;
}