package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.repository.DiscountRepository;
import com.example.jdshoes.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public Page<Discount> getAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable);
    }

    @Override
    public Optional<Discount> getDiscountById(Integer id){
        return discountRepository.findById(id);
    }

    @Override
    public Discount createDiscount (Discount discount){
        return discountRepository.save(discount);
    }

    @Override
    public Discount updateDiscount( Discount discount) {
        return discountRepository.save(discount);
    }
    @Override
    public void deleteDiscount(Integer id) {
        discountRepository.deleteById(id);
    }
}
