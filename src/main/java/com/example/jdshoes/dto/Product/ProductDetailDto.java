package com.example.jdshoes.dto.Product;



import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailDto {

    private Long id;
    private int quantity;
    private String barcode;
    private BigDecimal price;
    private int status;
    private Long productId;
    private Size size;
    private Color color;
    private String imageUrl;
}
