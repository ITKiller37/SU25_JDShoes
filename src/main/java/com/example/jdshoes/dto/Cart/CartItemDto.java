package com.example.jdshoes.dto.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemDto {
    private Long id; // ID của CartDetail
    private Long productDetailId; // ID của ProductDetail
    private int quantity; // Số lượng
}
