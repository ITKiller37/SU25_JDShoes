package com.example.jdshoes.entity;

import com.example.jdshoes.entity.enumClass.PaymentMethodName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PaymentMethod")
public class PaymentMethod {

    @Id
    private Long id;

    @Nationalized
    @Enumerated(EnumType.STRING)
    private PaymentMethodName name;

    private int status;
}
