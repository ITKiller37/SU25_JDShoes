package com.example.jdshoes.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nationalized
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String link;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;
}
