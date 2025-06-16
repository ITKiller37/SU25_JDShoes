package org.example.su25_jdshoes_ph48766.controller;

import org.example.su25_jdshoes_ph48766.entity.ProductDiscount;
import org.example.su25_jdshoes_ph48766.service.ProductDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin("*")
public class ProductDiscountController {

    @Autowired
    private ProductDiscountService discountService;

    @GetMapping
    public List<ProductDiscount> getAll() {
        return discountService.getAllDiscounts();
    }

    @GetMapping("/{id}")
    public ProductDiscount getById(@PathVariable Long id) {
        return discountService.getDiscountById(id);
    }

    @PostMapping
    public ProductDiscount create(@RequestBody ProductDiscount discount) {
        return discountService.createDiscount(discount);
    }

    @PutMapping("/{id}")
    public ProductDiscount update(@PathVariable Long id, @RequestBody ProductDiscount updated) {
        return discountService.updateDiscount(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        discountService.deleteDiscount(id);
    }
}
