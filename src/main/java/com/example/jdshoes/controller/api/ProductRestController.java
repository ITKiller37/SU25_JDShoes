package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

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
}
