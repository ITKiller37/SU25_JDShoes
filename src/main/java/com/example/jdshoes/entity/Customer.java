package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Nationalized
    private String name;
    private String phoneNumber;
    private String email;
    private String gender;
    private Date birthDay;
    private Boolean deleted = false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressShipping> addressShippings;

    @OneToOne(mappedBy = "customer",fetch = FetchType.LAZY)
    private Account account;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
