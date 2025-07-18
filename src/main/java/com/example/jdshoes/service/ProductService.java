package com.example.jdshoes.service;

import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {
    Page<ProductDto> getAllProductApi(Pageable pageable);

    ProductDto getByProductDetailId(Long detailId);

    Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable);

    Page<ProductDto> searchProduct(SearchProductDto searchDto, Pageable pageable);

    Page<ProductSearchDto> getAll(Pageable pageable);

    boolean existsByCode(String code);

    Product save(Product product) throws IOException;

    Product getProductByCode(String code);

    List<ProductDto> getAllProductNoPaginationApi(SearchProductDto searchRequest);

    Page<Product> getAllProduct(Pageable able);

    boolean existsByName(String name);

    Product findByCode(String maSanPham);

    void updateProductStatusBasedOnQuantity(Product product);

    ProductDto getProductDtoByCode(String code);

    ProductDto findById(Long id);
}
