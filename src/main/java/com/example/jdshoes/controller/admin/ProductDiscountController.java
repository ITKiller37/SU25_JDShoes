package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.ProductDiscount.ProductDiscountCreateDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductDiscountRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.service.CategoryService;
import com.example.jdshoes.service.ProductDiscountService;
import com.example.jdshoes.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
public class ProductDiscountController {

    private final ProductService productService;

    private final ProductDiscountService productDiscountService;
    private final CategoryService categoryService;
    private final ProductDetailRepository productDetailRepository;
    private final ProductDiscountRepository productDiscountRepository;

    public ProductDiscountController(ProductService productService, ProductRepository productRepository, ProductDiscountService productDiscountService, CategoryService categoryService, ProductDetailRepository productDetailRepository, ProductDiscountRepository productDiscountRepository) {
        this.productService = productService;
        this.productDiscountService = productDiscountService;
        this.categoryService = categoryService;
        this.productDetailRepository = productDetailRepository;
        this.productDiscountRepository = productDiscountRepository;
    }

    @GetMapping("/admin-only/product-discount")
    public String viewProductDiscountPage(Model model, Pageable pageable) {
        List<ProductDiscount> productDiscountList = productDiscountRepository.findAll();
        model.addAttribute("productDiscounts", productDiscountList);
        return "/admin/product-discount";
    }

    @ResponseBody
    @PostMapping("/api/private/product-discount/{id}/status/{status}")
    public ProductDiscountDto updateProductDiscount(@Valid @PathVariable Integer id, @PathVariable boolean status) {
        return productDiscountService.updateCloseProductDiscount(id, status);
    }

    @ResponseBody
    @PostMapping("/api/private/product-discount/multiple")
    public List<ProductDiscountDto> createProductDiscountMultiple(@Valid @RequestBody ProductDiscountCreateDto productDiscountCreateDto) {
        return productDiscountService.createProductDiscountMultiple(productDiscountCreateDto);
    }

    @GetMapping("/admin-only/product-discount-create")
    public String viewProductDiscountCreatePage(Model model) {
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "/admin/product-discount-create";
    }
}
