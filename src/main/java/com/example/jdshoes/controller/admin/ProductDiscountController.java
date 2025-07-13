package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.ProductDiscount.CreateProductDiscountRequest;
import com.example.jdshoes.dto.ProductDiscount.DiscountedProductDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.repository.ProductDiscountRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.service.ProductDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductDiscountController {
    private final ProductDiscountService productDiscountService;
    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;

    @DeleteMapping("/api/private/product-discount/{id}")
    @ResponseBody
    public String deleteProductDiscount(@PathVariable Long id) {
        System.out.println("Xóa ID: " + id);
        productDiscountService.deleteById(id);
        return "Xóa thành công";
    }

    @GetMapping("/admin-only/product-discount")
    public String showDiscounts(Model model) {
        List<ProductDiscountDto> discounts = productDiscountService.getAllDiscounts();
        model.addAttribute("discounts", discounts);
        return "admin/product-discount";
    }

    @GetMapping("/admin-only/product-discount-create")
    public String getProduct(Model model) {
        model.addAttribute("discount", new ProductDiscountDto());
        List<Product> productDetails = productRepository.findAll();
        model.addAttribute("productDetails", productDetails);
        return "admin/product-discount-create";
    }

    @PostMapping("/api/private/product-discount/multiple")
    @ResponseBody
    public String createMultipleDiscounts(@RequestBody @Valid CreateProductDiscountRequest request) {
        productDiscountService.createMultipleDiscounts(request);
        return "Thêm đợt giảm giá thành công";
    }

    @GetMapping("/admin-only/product-discount/{id}")
    public String editDiscountForm(@PathVariable Long id, Model model) {

        ProductDiscountDto discountDto = productDiscountService.getById(id);
        model.addAttribute("discount", discountDto);
        System.out.println("Mo ta" + discountDto.getDescription());

        List<Product> productList = productRepository.findAll();
        model.addAttribute("productDetails", productList);


        List<DiscountedProductDto> appliedProducts = productDiscountService.getDiscountedProductDtosByDiscountId(id);
        model.addAttribute("products", appliedProducts);

        return "admin/product-discount-edit";
    }

//    @PutMapping("/api/private/product-discount/{id}")
//    public String updateProductDiscount(@PathVariable Long id,
//                                        @RequestBody @Valid CreateProductDiscountRequest request) {
//        productDiscountService.updateDiscount(id, request);
//        return "Cập nhật đợt giảm giá thành công";
//    }

}

