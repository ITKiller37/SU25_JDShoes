package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.repository.CategoryRepository;
import com.example.jdshoes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategoryApi(CategoryDto categoryDto) {
        Category category = convertToEntity(categoryDto);
        category.setStatus(1);
        category.setDeleteFlag(false);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    private Category convertToEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCode(categoryDto.getCode());
        category.setName(categoryDto.getName());
        category.setId(categoryDto.getId());
        return category;
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCode(category.getCode());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
