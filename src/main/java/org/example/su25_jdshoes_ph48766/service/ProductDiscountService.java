package org.example.su25_jdshoes_ph48766.service;

import org.example.su25_jdshoes_ph48766.entity.ProductDiscount;

import java.util.List;

public interface ProductDiscountService {
    List<ProductDiscount> getAllDiscounts();

    ProductDiscount getDiscountById(Long id);

    ProductDiscount createDiscount(ProductDiscount discount);

    ProductDiscount updateDiscount(Long id, ProductDiscount updated);

    void deleteDiscount(Long id);
}
