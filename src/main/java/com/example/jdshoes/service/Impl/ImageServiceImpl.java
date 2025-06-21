package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.*;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ImageRepository;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;


    @Override
    public List<Image> getAllImagesByProductDetailId(Long productDetailId) {
        return imageRepository.findAllByProductDetailId(productDetailId);
    }
}
