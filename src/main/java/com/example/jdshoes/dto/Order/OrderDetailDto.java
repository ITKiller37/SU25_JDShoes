package com.example.jdshoes.dto.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetailDto {
    private Integer quantity;
    private Long productDetailId;
}
