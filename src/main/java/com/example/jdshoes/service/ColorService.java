package com.example.jdshoes.service;

import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ColorService {

    List<Color> getColorByProductId(Long productId);

    List<Color> getColorByProductIdAndSizeId(Long productId, Long sizeId);

    ColorDto createColorApi(ColorDto colorDto);

    Page<Color> findAll(Pageable pageable);

    Color createColor(Color color);

    boolean existsById(Long id);

    Color updateColor(Color color);

    Optional<Color> findById(Long id);


    void delete(Long id);

    List<Color> findAll();
}
