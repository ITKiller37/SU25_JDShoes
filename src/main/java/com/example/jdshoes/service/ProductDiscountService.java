package com.example.jdshoes.service;

import com.example.jdshoes.dto.ProductDiscount.ProductDiscountCreateDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;

import java.util.List;

public interface ProductDiscountService {

    ProductDiscountDto updateCloseProductDiscount(Integer discountId, boolean closed);

    List<ProductDiscountDto> createProductDiscountMultiple(ProductDiscountCreateDto productDiscountCreateDto);

    void deleteById(Integer id);

}
