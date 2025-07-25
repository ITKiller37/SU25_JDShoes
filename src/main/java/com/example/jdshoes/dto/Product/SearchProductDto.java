package com.example.jdshoes.dto.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductDto {
    private String code;
    private Double minPrice;
    private Double maxPrice;
    private String productName;
    private List<Long> categoryId;
    private String brandName;
    private String keyword;
    private String barcode;
    private String sort;
    private BigDecimal discountedPrice;


}
