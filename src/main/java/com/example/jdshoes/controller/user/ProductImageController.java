package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/images")
public class ProductImageController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}/product/{colorId}/color")
    public List<ImageDto> getImagesByColor(@PathVariable Long productId, @PathVariable Long colorId) {
        ProductDto product = productService.findById(productId);

        return product.getProductDetailDtos().stream()
                .filter(p -> p.getColorId().equals(colorId))
                .findFirst()
                .map(ProductDetailDto::getImages)
                .orElse(Collections.emptyList());
    }


}
