package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.entity.ProductDiscountDetail;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductDiscountDetailRepository;
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
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private ProductDiscountDetailRepository productDiscountDetailRepository;

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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<ProductDetail> productDetails = productDetailRepository.getProductDetailByProductId(id);
        List<ProductDetailDto> productDetailDTOs = new ArrayList<>();

        for (ProductDetail productDetail : productDetails) {
            ProductDetailDto dto = new ProductDetailDto();
            dto.setId(productDetail.getId());
            dto.setProductId(productDetail.getProduct().getId());
            dto.setPrice(productDetail.getPrice());
            dto.setSize(productDetail.getSize());
            dto.setColor(productDetail.getColor());
            dto.setQuantity(productDetail.getQuantity());

            // 🔍 Tìm ProductDiscountDetail (đang active)
            ProductDiscountDetail discountDetail = productDiscountDetailRepository
                    .findValidByProductDetailId(productDetail.getId());

            if (discountDetail != null && discountDetail.getDiscountedAmount() != null) {
                dto.setDiscountedPrice(discountDetail.getDiscountedAmount()); // ✅ giá sau khi giảm
                dto.setDiscountedAmount(productDetail.getPrice().subtract(discountDetail.getDiscountedAmount())); // ✅ tiền đã giảm (tuỳ chọn)
            }

            productDetailDTOs.add(dto);
        }

        return productDetailDTOs;
    }
}
