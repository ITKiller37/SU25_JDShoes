package com.example.jdshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "AddressShipping")
public class AddressShipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    private String street;

    @Nationalized
    private String ward;

    @Nationalized
    private String district;

    @Nationalized
    private String province;

    private Boolean isDefault = false;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }


    @Nationalized
    @Column(nullable = false, length = 150)
    private String address;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;
}
