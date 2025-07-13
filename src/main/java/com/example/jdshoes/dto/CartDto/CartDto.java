package com.example.jdshoes.dto.CartDto;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long id;
    private Long customerId;
    private ProductCart product;
    private ProductDetailDto detail;

    @NotNull
    private int quantity;
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;
}
