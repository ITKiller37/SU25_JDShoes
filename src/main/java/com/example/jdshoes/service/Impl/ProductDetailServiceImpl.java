package com.example.jdshoes.service.Impl;

import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.service.ProductDetailService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDetailRepository productDetailRepository;

    public ProductDetailServiceImpl(ProductDetailRepository productDetailRepository) {
        this.productDetailRepository = productDetailRepository;
    }

    @Override
    public Optional<ProductDetail> findById(Long id) {
        return productDetailRepository.findById(id);
    }

    @Override
    public ProductDetail save(ProductDetail productDetail) {
        return productDetailRepository.save(productDetail);
    }

    @Override
    public void delete(Long id) {
        productDetailRepository.deleteById(id);
    }
}
