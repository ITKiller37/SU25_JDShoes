package com.example.jdshoes.service;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.ProductDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductDetailService {
    // Tìm ProductDetail theo ID
    Optional<ProductDetail> findById(Long id);

    // Lưu hoặc cập nhật một ProductDetail
    ProductDetail save(ProductDetail productDetail);

    // Xóa một ProductDetail theo ID
    void delete(Long id);

//    List<ProductDetailDto> getByProductId(Long id) throws com.project.DuAnTotNghiep.exception.NotFoundException;
}
