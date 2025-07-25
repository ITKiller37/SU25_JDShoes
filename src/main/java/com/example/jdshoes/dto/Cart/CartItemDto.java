package com.example.jdshoes.dto.Cart;

import com.example.jdshoes.dto.CartDto.ProductCart;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemDto {
    private Long id; // ID của CartDetail
    private Long productDetailId; // ID của ProductDetail
    private int quantity; // Số lượng
}
