
package com.example.jdshoes.service;

import com.example.jdshoes.dto.ProductDiscount.CreateProductDiscountRequest;
import com.example.jdshoes.dto.ProductDiscount.DiscountedProductDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
import com.example.jdshoes.entity.ProductDiscount;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductDiscountService {
    List<ProductDiscountDto> getAllDiscounts();

    // Thêm mới
    void toggleClosed(Long id);

    ProductDiscount createDiscount(ProductDiscountDto dto);
    void createMultipleDiscounts(CreateProductDiscountRequest request);


    ProductDiscountDto getById(Long id);

    List<DiscountedProductDto> getDiscountedProductDtosByDiscountId(Long id);

    void updateDiscount(Long id, @Valid CreateProductDiscountRequest request);
}

