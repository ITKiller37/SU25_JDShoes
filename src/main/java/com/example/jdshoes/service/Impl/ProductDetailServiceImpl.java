package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductDiscountRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDetailRepository productDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDiscountRepository productDiscountRepository;

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

    @Override
    public List<ProductDetailDto> getByProductId(Long id) throws NotFoundException {
        Product product = productRepository.findById(id).orElseThrow( () -> new NotFoundException("Product not found"));
        List<ProductDetail> productDetails = productDetailRepository.getProductDetailByProductId(id);
        List<ProductDetailDto> productDetailDTOs = new ArrayList<>();

        for (ProductDetail productDetail : productDetails) {
            ProductDetailDto productDetailDTO = new ProductDetailDto();
            // Set properties of productDetailDTO based on productDetail
            productDetailDTO.setId(productDetail.getId());
            productDetailDTO.setProductId(productDetail.getProduct().getId());
            productDetailDTO.setPrice(productDetail.getPrice());
            productDetailDTO.setSize(productDetail.getSize());
            productDetailDTO.setColor(productDetail.getColor());
            productDetailDTO.setQuantity(productDetail.getQuantity());

            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
            if(productDiscount != null) {
//                Date endDate = productDiscount.getEndDate();
//                Date currentDate = new Date();
//                if (currentDate.compareTo(endDate) > 0) {
//                }
                productDetailDTO.setDiscountedPrice(productDiscount.getDiscountedAmount());

            }
            // Set other properties as needed
            productDetailDTOs.add(productDetailDTO);
        }
        return productDetailDTOs;
    }
}
