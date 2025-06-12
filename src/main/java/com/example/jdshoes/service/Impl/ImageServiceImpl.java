package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
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
    public List<Image> getAllImagesByProductDetailId(Long productDetailId, Size size, Color color) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductIdAndSizeAndColor(productDetailId, size, color);
        if (productDetails.isEmpty()) {
            throw new NotFoundException("No product details found for productId: " + productDetailId + ", size: " + size + ", color: " + color);
        }
        return imageRepository.findAllByProductDetail(productDetails.get(0));
    }
}
