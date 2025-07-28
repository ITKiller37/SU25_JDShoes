package com.example.jdshoes.dto.CartDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCart {
    private Long productId;
    private String code;
    private String name;
    private String imageUrl; // URL của ảnh chính

}
