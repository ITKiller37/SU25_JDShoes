package com.example.jdshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckOrderDto {
    private Long productDetailId;
    private String quantity;
}
