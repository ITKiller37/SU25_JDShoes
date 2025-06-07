package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Material;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.repository.SizeRepository;
import com.example.jdshoes.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public SizeDto createSizeApi(SizeDto sizeDto) {

        Size size = convertToEntity(sizeDto);
        size.setDeleteFlag(false);
        Size savedSize = sizeRepository.save(size);
        return convertToDto(savedSize);
    }

    @Override
    public List<Size> getSizesByProductId(Long productId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return sizeRepository.findSizesByProduct(product);
    }

    @Override
    public List<Size> getSizesByProductIdAndColorId(Long productId, Long colorId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Color color = colorRepository.findById(colorId).orElseThrow(() -> new NotFoundException("Color not found"));;

        return sizeRepository.findSizesByProductAndColor(product, color);
    }

    private Size convertToEntity(SizeDto sizeDto) {
        Size size = new Size();
        size.setId(sizeDto.getId());
        size.setName(sizeDto.getName());
        size.setCode(sizeDto.getCode());
        return size;
    }

    private SizeDto convertToDto(Size size) {
        SizeDto sizeDto = new SizeDto();
        sizeDto.setId(size.getId());
        sizeDto.setName(size.getName());
        sizeDto.setCode(size.getCode());
        return sizeDto;
    }
}
