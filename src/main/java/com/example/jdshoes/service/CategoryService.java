package com.example.jdshoes.service;



import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {

    CategoryDto createCategoryApi(CategoryDto categoryDto);

    Page<Category> getAllCategory(Pageable pageable);

    Category createCategory(Category category);

    boolean existsById(Long id);

    Optional<Category> findById(Long id);

    Category updateCategory(Category category);

    void delete(Long id);

    List<Category> getAll();
}
