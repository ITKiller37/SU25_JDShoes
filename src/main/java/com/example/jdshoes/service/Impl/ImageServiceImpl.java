package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.Image;
import com.example.jdshoes.repository.ImageRepository;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Optional<Image> findById(Long id) {
        return imageRepository.findById(id);
    }

    @Override
    public Image save(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public void delete(Long id) {
        imageRepository.deleteById(id);
    }
}
