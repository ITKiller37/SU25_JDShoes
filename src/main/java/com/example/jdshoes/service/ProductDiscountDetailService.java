package com.example.jdshoes.service;

import com.example.jdshoes.entity.ProductDiscountDetail;

import java.util.List;

public interface ProductDiscountDetailService {
    void addProductToDiscount(Long discountId, List<Long> productDetailIds);
    void removeProductFromDiscount(Long discountDetailId);
    List<ProductDiscountDetail> findAllByDiscountId(Long discountId);
}
