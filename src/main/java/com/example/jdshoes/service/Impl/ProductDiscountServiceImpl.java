package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.ProductDiscount.ProductDiscountCreateDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductDiscountRepository;
import com.example.jdshoes.service.ProductDiscountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDetailRepository productDetailRepository;

    private final ProductDiscountRepository productDiscountRepository;

    public ProductDiscountServiceImpl(ProductDetailRepository productDetailRepository, ProductDiscountRepository productDiscountRepository) {
        this.productDetailRepository = productDetailRepository;
        this.productDiscountRepository = productDiscountRepository;
    }

    @Override
    public ProductDiscountDto updateCloseProductDiscount(Integer discountId, boolean closed) {
        ProductDiscount productDiscount = productDiscountRepository.findById(discountId).orElseThrow(() -> new NotFoundException("Product discount not found"));
        productDiscount.setClosed(closed);
        return convertToDto(productDiscountRepository.save(productDiscount));
    }
    @Override
    public List<ProductDiscountDto> createProductDiscountMultiple(ProductDiscountCreateDto productDiscountCreateDto) {
        List<ProductDiscount> productDiscountList = productDiscountCreateDto.getProductDiscounts().stream().map(productDiscountDto -> {
            ProductDiscount productDiscount = productDiscountRepository.findByProductDetail_Id(productDiscountDto.getProductDetailId());
            if(productDiscount != null) {
                productDiscountDto.setId(productDiscount.getId());
            }
            productDiscountDto.setClosed(false);
            return convertToEntity(productDiscountDto);
        }).collect(Collectors.toList());
        productDiscountRepository.saveAll(productDiscountList);
        return productDiscountCreateDto.getProductDiscounts();
    }

    private ProductDiscount convertToEntity(ProductDiscountDto productDiscountDto) {
        ProductDiscount productDiscount = new ProductDiscount();
        productDiscount.setId(productDiscountDto.getId());
        productDiscount.setDiscountedAmount(productDiscountDto.getDiscountedAmount());
        productDiscount.setClosed(productDiscountDto.isClosed());
        productDiscount.setStartDate(productDiscountDto.getStartDate());
        productDiscount.setEndDate(productDiscountDto.getEndDate());
        ProductDetail productDetail = productDetailRepository.findById(productDiscountDto.getProductDetailId()).orElseThrow(() -> new NotFoundException("Product detail id not found"));
        productDiscount.setProductDetail(productDetail);
        return productDiscount;
    }

    private ProductDiscountDto convertToDto(ProductDiscount productDiscount) {
        ProductDiscountDto productDiscountDto = new ProductDiscountDto();
        productDiscountDto.setId(productDiscount.getId());
        productDiscountDto.setDiscountedAmount(productDiscount.getDiscountedAmount());
        productDiscountDto.setClosed(productDiscount.isClosed());
        productDiscountDto.setStartDate(productDiscount.getStartDate());
        productDiscountDto.setEndDate(productDiscount.getEndDate());
        productDiscountDto.setProductDetailId((productDiscount.getProductDetail().getId()));
        return productDiscountDto;
    }
}
