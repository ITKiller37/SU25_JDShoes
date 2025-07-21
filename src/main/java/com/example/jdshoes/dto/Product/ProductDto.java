package com.example.jdshoes.dto.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String categoryName;
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;
    private BigDecimal priceMin;
    private boolean isDiscounted;
    private BigDecimal discountedPrice;


    private String imageUrl;

    private List<ProductDetailDto> productDetailDtos;
}
