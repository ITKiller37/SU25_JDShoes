package com.example.jdshoes.entity;

import com.example.jdshoes.entity.enumClass.RoleName;
import lombok.*;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "Role")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private RoleName name;
    private Date createDate;
    private Date updateDate;
}