package com.example.jdshoes.entity;

import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;



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

    @Nationalized
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    private String gender;

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