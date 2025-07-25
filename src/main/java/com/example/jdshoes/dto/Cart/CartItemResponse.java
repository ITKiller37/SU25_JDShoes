package com.example.jdshoes.dto.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemResponse {

    private Long productDetailId;
    private Integer quantity;
    private String productName;
    private String sizeName;
    private String colorName;
    private BigDecimal price;
    private List<String> images;
}
