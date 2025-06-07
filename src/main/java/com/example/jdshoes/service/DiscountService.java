package com.example.jdshoes.service;

import com.example.jdshoes.entity.Discount;

import java.util.List;
import java.util.Optional;

public interface DiscountService {

    List<Discount> getAllDiscounts();
    Optional<Discount> getDiscountById(Integer id);
    Discount createDiscount(Discount discount);
    Discount updateDiscount(Integer id, Discount discount);
    void deleteDiscount(Integer id);

}
