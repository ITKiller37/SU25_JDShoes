package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.repository.SizeRepository;
import com.example.jdshoes.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Color> getColorByProductId(Long productId) throws NotFoundException {

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return colorRepository.findColorsByProduct(product);
    }

    @Override
    public List<Color> getColorByProductIdAndSizeId(Long productId, Long sizeId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new NotFoundException("Size not found"));
        return colorRepository.findColorsByProductAndSize(product, size);
    }

    @Override
    public ColorDto createColorApi(ColorDto colorDto) {

        Color color = convertToEntity(colorDto);
        color.setDeleteFlag(false);
        Color savedColor = colorRepository.save(color);
        return convertToDto(savedColor);
    }

    private Color convertToEntity(ColorDto colorDto) {
        Color color = new Color();
        color.setCode(colorDto.getCode());
        color.setName(colorDto.getName());
        color.setId(colorDto.getId());
        return color;
    }

    private ColorDto convertToDto(Color color) {
        ColorDto colorDto = new ColorDto();
        colorDto.setId(color.getId());
        colorDto.setCode(color.getCode());
        colorDto.setName(color.getName());
        return colorDto;
    }
}
