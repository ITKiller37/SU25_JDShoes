package com.example.jdshoes.service;

import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Color;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ColorService {

    List<Color> getColorByProductId(Long productId);
    List<Color> getColorByProductIdAndSizeId(Long productId, Long sizeId);
    ColorDto createColorApi(ColorDto colorDto);
}
