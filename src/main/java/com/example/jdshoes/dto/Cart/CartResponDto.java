package com.example.jdshoes.dto.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponDto {
    private Long cartId;
    private Long customerId;
    private List<CartItemDto> items;
}
