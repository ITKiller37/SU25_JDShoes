package com.example.jdshoes.service;

import com.example.jdshoes.dto.Product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDto> getAllProductApi(Pageable pageable);
    ProductDto getByProductDetailId(Long detailId);
}
