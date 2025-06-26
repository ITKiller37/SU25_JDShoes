package com.example.jdshoes.service.serviceImpl;


import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.exception.ShopApiException;
import com.example.jdshoes.repository.CategoryRepository;
import com.example.jdshoes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<Category> getAllCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category createCategory(Category category) {
        if(categoryRepository.existsByCode(category.getCode())) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã loại " + category.getCode() + " đã tồn tại");
        }
        category.setDeleteFlag(false);
        return categoryRepository.save(category);
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category updateCategory(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElseThrow(null);
        if(!existingCategory.getCode().equals(category.getCode())) {
            if(categoryRepository.existsByCode(category.getCode())) {
                throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã loại " + category.getCode() + " đã tồn tại");
            }
        }
        category.setDeleteFlag(false);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(null);
        category.setDeleteFlag(true);
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
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
