package com.example.jdshoes.service;

import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDto> getAllProductApi(Pageable pageable);
    ProductDto getByProductDetailId(Long detailId);
    Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable);
    Page<ProductSearchDto> getAll(Pageable pageable);
}
