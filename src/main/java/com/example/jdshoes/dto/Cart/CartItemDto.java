package com.example.jdshoes.dto.Cart;

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
    private Long id;
    private Long customerId;
    private Long productDetailId;
    private int quantity;
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;
}
