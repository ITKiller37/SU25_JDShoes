package com.example.jdshoes.controller;

import com.example.jdshoes.dto.DiscountDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/discounts")
    public List<Discount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }
    @PostMapping("/addDiscount")
    public Discount addDiscount(@RequestBody Discount discount) {
        return discountService.createDiscount(discount);
    }
    @PostMapping("/updateDiscount")
    public Discount updateDiscount(@RequestBody Discount discount) {
        return discountService.createDiscount(discount);
    }
    @GetMapping("/deleteDiscount")
    public void deleteDiscount(@RequestParam("id") Integer id) {
        discountService.deleteDiscount(id);
    }
    @GetMapping("/detailDiscount/{id}")
    public Discount getDiscountById(@PathVariable("id") Integer id) {
        return discountService.getDiscountById(id).get();
    }
}
