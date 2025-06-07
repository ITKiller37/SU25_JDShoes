package com.example.jdshoes.service;

import com.example.jdshoes.dto.Category.CategoryDto;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    CategoryDto createCategoryApi(CategoryDto categoryDto);
}
