package com.example.jdshoes.dto.CartDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCart {
    private Long productId;
    private String code;
    private String name;
    private Long materialId;
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;
    private String describe;
    private Long brandId;
    private Long categoryId;
    private String imageUrl;

}
