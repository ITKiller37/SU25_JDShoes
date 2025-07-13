package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping("/api/category")
    public CategoryDto createCategoryApi(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategoryApi(categoryDto);
    }
}
