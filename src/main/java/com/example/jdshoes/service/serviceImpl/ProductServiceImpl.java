package com.example.jdshoes.service.serviceImpl;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.service.ProductService;
import com.example.jdshoes.utils.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;


    @Override
    public Page<ProductDto> getAllProductApi(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByDeleteFlagFalse(pageable);
        return productPage.map(this::convertToDto);
    }

    @Override
    public ProductDto getByProductDetailId(Long detailId) {
        return convertToDto(productRepository.findByProductDetail_Id(detailId));
    }

    @Override
    public Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable) {
        Page<ProductSearchDto> productSearchDtos = productRepository.listSearchProduct(maSanPham,tenSanPham,nhanHang,chatLieu,theLoai,trangThai,pageable);
        return productSearchDtos;
    }

    @Override
    public Page<ProductSearchDto> getAll(Pageable pageable) {
        return productRepository.getAll(pageable);
    }

    @Override
    public boolean existsByCode(String code) {
        return productRepository.existsByCode(code);
    }

    @Override
    public Product save(Product product) throws IOException {

        if(product.getCode().trim() == "" || product.getCode() == null) {
            Product productCurrent = productRepository.findTopByOrderByIdDesc();
            Long nextCode = (productCurrent == null) ? 1 : productCurrent.getId() + 1;
            String productCode = "SP" + String.format("%04d", nextCode);
            product.setCode(productCode);
        }

        BigDecimal minPrice = new BigDecimal("1000000000");
        for (ProductDetail productDetail : product.getProductDetails()) {
            if (productDetail.getPrice().compareTo(minPrice) < 0) {
                minPrice = productDetail.getPrice();
            }
            QRCodeService.generateQRCode(productDetail.getBarcode(), productDetail.getBarcode());
        }

        product.setPrice(minPrice);
        product.setDeleteFlag(false);
        product.setCreateDate(LocalDateTime.now());
        product.setUpdatedDate(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product getProductByCode(String code) {
        Product product = productRepository.findByCode(code);
        if(product != null) {

            return product;
        }
        return null;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setCode(product.getCode());
        productDto.setName(product.getName());
        productDto.setCategoryName(product.getCategory().getName());
        productDto.setDescription(product.getDescription());
        productDto.setCreateDate(product.getCreateDate());
        productDto.setUpdatedDate(product.getUpdatedDate());

        List<ProductDetailDto> productDetailDtoList = new ArrayList<>();
        BigDecimal priceMin = new BigDecimal("100000000");
        for (ProductDetail productDetail : product.getProductDetails()) {
            if (productDetail.getPrice().compareTo(priceMin) < 0) {
                priceMin = productDetail.getPrice();
            }
            ProductDetailDto productDetailDto = new ProductDetailDto();
            productDetailDto.setId(productDetail.getId());
            productDetailDto.setProductId(product.getId());
            productDetailDto.setColor(productDetail.getColor());
            productDetailDto.setSize(productDetail.getSize());
            productDetailDto.setPrice(productDetail.getPrice());
            productDetailDto.setQuantity(productDetail.getQuantity());
            productDetailDto.setBarcode(productDetail.getBarcode());
            productDetailDtoList.add(productDetailDto);
        }
        productDto.setPriceMin(priceMin);
        productDto.setProductDetailDtos(productDetailDtoList);
        return productDto;
    }
}
