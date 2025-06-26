package com.example.jdshoes.controller.api;



import com.example.jdshoes.dto.Brand.BrandDto;
import com.example.jdshoes.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class BrandRestController {
    private final BrandService brandService;

    @PostMapping("/api/brand")
    public BrandDto createBrandApi(@Valid @RequestBody BrandDto brandDto) {
        return brandService.createBrandApi(brandDto);
    }
}
