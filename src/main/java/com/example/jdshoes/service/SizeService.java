package com.example.jdshoes.service;

import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SizeService {
    SizeDto createSizeApi(SizeDto sizeDto);
    List<Size> getSizesByProductId(Long productId);
    List<Size> getSizesByProductIdAndColorId(Long productId, Long colorId);

    Page<Size> getAllSize(Pageable pageable);

    Size createSize(Size size);

    Size updateSize(Size size);

    Optional<Size> findById(Long id);

    void delete(Long id);

    List<Size> getAll();
}
