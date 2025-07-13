package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.exception.ShoesApiException;
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

        // Kiểm tra tên và mã khi tạo Category qua API
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên loại");
        }
        if (categoryRepository.existsByName(categoryDto.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên loại '" + categoryDto.getName() + "' đã tồn tại");
        }

        if (categoryDto.getCode() == null || categoryDto.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã loại");
        }
        if (categoryRepository.existsByCode(categoryDto.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã loại '" + categoryDto.getCode() + "' đã tồn tại");
        }

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
        // Kiểm tra mã
        if (category.getCode() == null || category.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã loại sản phẩm");
        }
        if(categoryRepository.existsByCode(category.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã loại sản phẩm '" + category.getCode() + "' đã tồn tại");
        }

        // THÊM KIỂM TRA TÊN KHI THÊM
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên loại sản phẩm");
        }
        if (categoryRepository.existsByName(category.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên loại sản phẩm '" + category.getName() + "' đã tồn tại");
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
        // Lấy category hiện có từ DB
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ShoesApiException(HttpStatus.NOT_FOUND, "Không tìm thấy loại sản phẩm với ID: " + category.getId())); // Thêm thông báo lỗi rõ ràng

        // Kiểm tra mã loại
        if (category.getCode() == null || category.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã loại sản phẩm");
        }
        // Chỉ kiểm tra trùng mã nếu mã mới khác mã cũ
        if(!existingCategory.getCode().equals(category.getCode().trim())) {
            if(categoryRepository.existsByCode(category.getCode().trim())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã loại sản phẩm '" + category.getCode() + "' đã tồn tại");
            }
        }

        // THÊM KIỂM TRA TÊN KHI SỬA
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên loại sản phẩm");
        }
        // Chỉ kiểm tra trùng tên nếu tên mới khác tên cũ, và tên đó không phải của chính bản ghi đang sửa
        if (!existingCategory.getName().equals(category.getName().trim())) {
            if (categoryRepository.existsByNameAndIdNot(category.getName().trim(), category.getId())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên loại sản phẩm '" + category.getName() + "' đã tồn tại");
            }
        }

        // Cập nhật các trường của existingCategory
        existingCategory.setCode(category.getCode().trim());
        existingCategory.setName(category.getName().trim());
        existingCategory.setStatus(category.getStatus()); // Giữ nguyên trạng thái hoặc cập nhật nếu có từ request
        existingCategory.setDeleteFlag(false); // Giữ nguyên deleteFlag hoặc cập nhật nếu có từ request

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
