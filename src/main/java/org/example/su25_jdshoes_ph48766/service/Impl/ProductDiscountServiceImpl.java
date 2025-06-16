package org.example.su25_jdshoes_ph48766.service.Impl;

import org.example.su25_jdshoes_ph48766.entity.ProductDiscount;
import org.example.su25_jdshoes_ph48766.repository.ProductDiscountRepo;
import org.example.su25_jdshoes_ph48766.service.ProductDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {
    @Autowired
    private ProductDiscountRepo discountRepository;

    @Override
    public List<ProductDiscount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public ProductDiscount getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + id));
    }

    @Override
    public ProductDiscount createDiscount(ProductDiscount discount) {
        return discountRepository.save(discount);
    }

    @Override
    public ProductDiscount updateDiscount(Long id, ProductDiscount updated) {
        ProductDiscount existing = getDiscountById(id);

        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setValue(updated.getValue());
        existing.setStatus(updated.getStatus());
        existing.setDescription(updated.getDescription());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());

        return discountRepository.save(existing);
    }

    @Override
    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }
}
