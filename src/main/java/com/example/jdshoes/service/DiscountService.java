package com.example.jdshoes.service;

import com.example.jdshoes.dto.Discount.DiscountDto;
import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DiscountService {

    List <Discount> findAll();
    Page<Discount> getAllDiscounts(Pageable pageable);
    Page<Discount> searchDiscounts(SearchDiscountCodeDto searchDto, Pageable pageable);
    Optional<Discount> getDiscountById(Integer id);
    Discount createDiscount(Discount discount);
    void updateDiscount( Discount discount);
    Discount findById(Integer id);

    Page<DiscountDto> getAllDiscountCode(SearchDiscountCodeDto searchDiscountCodeDto, Pageable pageable);

    Page<DiscountDto> getAllAvailableDiscountCode(Pageable pageable);

//    Discount updateStatus(Integer discountCodeId, int status);

}