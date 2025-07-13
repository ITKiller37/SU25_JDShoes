package com.example.jdshoes.entity;

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
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Nationalized
    private String name;

    @Nationalized
    private String description;
    private int status;

    @Column(nullable = false)
    private boolean deleteFlag;

    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "materialId")
    private Material material;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> productDetails;
}
