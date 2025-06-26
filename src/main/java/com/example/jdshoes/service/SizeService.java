package com.example.jdshoes.service;


import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SizeService {

    // Tạo size từ DTO (API sử dụng)
    SizeDto createSizeApi(SizeDto sizeDto);

    // Lấy size theo sản phẩm
    List<Size> getSizesByProductId(Long productId);

    // Lấy size theo sản phẩm và màu
    List<Size> getSizesByProductIdAndColorId(Long productId, Long colorId);

    // Lấy tất cả size phân trang
    Page<Size> getAllSize(Pageable pageable);

    // Tạo size từ entity
    Size createSize(Size size);

    // Cập nhật size
    Size updateSize(Size size);

    // Tìm size theo id
    Optional<Size> findById(Long id);

    // Xoá size theo id
    void delete(Long id);

    // Lấy tất cả size (không phân trang)
    List<Size> getAll();
}
