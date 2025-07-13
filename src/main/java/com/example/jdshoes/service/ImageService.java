package com.example.jdshoes.service;

import com.example.jdshoes.entity.Image;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ImageService {


    // Lấy tất cả ảnh của một ProductDetail cụ thể
    List<Image> getAllImagesByProductDetailId(Long productDetailId);

    // Tìm ảnh theo ID
    Optional<Image> findById(Long id);

    // Lưu hoặc cập nhật một ảnh
    Image save(Image image);

    // Xóa một ảnh theo ID
    void delete(Long id);
}
