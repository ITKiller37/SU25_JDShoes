package com.example.jdshoes.service;

import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Size;

import java.util.List;

public interface SizeService {
    SizeDto createSizeApi(SizeDto sizeDto);
    List<Size> getSizesByProductId(Long productId);
    List<Size> getSizesByProductIdAndColorId(Long productId, Long colorId);
}
