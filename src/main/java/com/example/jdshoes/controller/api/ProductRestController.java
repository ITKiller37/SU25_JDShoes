package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @GetMapping("/api/products")
    public Page<ProductDto> getAllProductsApi(@PageableDefault(page = 0, size = 12) Pageable pageable) {
        return productService.getAllProductApi(pageable);
    }

    @GetMapping("/api/products/{detailId}/productDetail")
    public ProductDto getByProductDetailId(@PathVariable Long detailId) {
        return productService.getByProductDetailId(detailId);
    }
    @GetMapping("/api/products-no-pagination")
    public List<ProductDto> getAllProductsApi(SearchProductDto searchRequest) {
        return productService.getAllProductNoPaginationApi(searchRequest);
    }

    @GetMapping("/api/products/filter")
    public Page<ProductDto> filterProductApi(SearchProductDto searchDto, @PageableDefault(page = 0, size = 10) Pageable pageable){
        return productService.searchProduct(searchDto, pageable);
    }

    @GetMapping("/api/product/check-code")
    @ResponseBody
    public Map<String, Boolean> checkProductCode(@RequestParam String code) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", productService.existsByCode(code));
        return response;
    }

    @GetMapping("api/product/check-name")
    @ResponseBody
    public Map<String, Boolean> checkProductNameExists(@RequestParam String name) {
        boolean exists = productService.existsByName(name);
        return Collections.singletonMap("exists", exists);
    }

    @PutMapping("api/product-details/{id}/quantity")
    public ResponseEntity<?> updateProductDetailQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            ProductDetail productDetail = productDetailRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

            productDetail.setQuantity(request.get("quantity"));
            productDetailRepository.save(productDetail);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}