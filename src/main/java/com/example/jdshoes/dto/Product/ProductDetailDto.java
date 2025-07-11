package com.example.jdshoes.dto.Product;

import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Size;
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
public class ProductDetailDto {

    private Long id;
    private int quantity;
    private String barcode;
    private BigDecimal price;
    private Long productId;
    private String colorName;
    private String sizeName;

    private String productName;
    private String productDescription;
    private String brandName;
    private String materialName;
    private String categoryName;

    private BigDecimal discountedAmount;
    private Long colorId;
    private List<ImageDto> images;
}
