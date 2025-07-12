package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.repository.Specification.ProductSpecification;
import com.example.jdshoes.service.ProductService;
import com.example.jdshoes.utils.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return convertToDto(productRepository.findByProductDetailId(detailId));
    }

    @Override
    public Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable) {
        Page<ProductSearchDto> productSearchDtos = productRepository.listSearchProduct(maSanPham, tenSanPham, nhanHang, chatLieu, theLoai, trangThai, pageable);
        return productSearchDtos;
    }

    @Override
    public Page<ProductDto> searchProduct(SearchProductDto searchDto, Pageable pageable) {
        Specification<Product> spec = new ProductSpecification(searchDto);
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::convertToDto);
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

        if (product.getCode().trim() == "" || product.getCode() == null) {
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
        if (product != null) {

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
            if (productDetail.getQuantity() > 0) { // Chỉ thêm biến thể có quantity > 0
                if (productDetail.getPrice().compareTo(priceMin) < 0) {
                    priceMin = productDetail.getPrice();
                }
                ProductDetailDto productDetailDto = new ProductDetailDto();
                productDetailDto.setId(productDetail.getId());
                productDetailDto.setProductId(product.getId());
                productDetailDto.setColorName(productDetail.getColor().getName());
                productDetailDto.setSizeName(productDetail.getSize().getName());
                productDetailDto.setPrice(productDetail.getPrice());
                productDetailDto.setQuantity(productDetail.getQuantity());
                productDetailDto.setBarcode(productDetail.getBarcode());


                // Ánh xạ danh sách ảnh
                if (productDetail.getImages() != null && !productDetail.getImages().isEmpty()) {
                    List<ImageDto> imageDtos = productDetail.getImages().stream()
                            .map(image -> {
                                ImageDto imageDto = new ImageDto();
                                imageDto.setLink(image.getLink()); // Giả sử Image có phương thức getUrl()
                                return imageDto;
                            })
                            .collect(Collectors.toList());
                    productDetailDto.setImages(imageDtos);
                } else {
                    productDetailDto.setImages(new ArrayList<>()); // Đặt danh sách rỗng nếu không có ảnh

                }

                productDetailDtoList.add(productDetailDto);
            }
        }
        productDto.setPriceMin(priceMin);
        productDto.setProductDetailDtos(productDetailDtoList);
        return productDto;

    }

    @Override
    public List<ProductDto> getAllProductNoPaginationApi(SearchProductDto searchRequest) {
        Specification<Product> spec = new ProductSpecification(searchRequest);
        List<Product> products = productRepository.findAll(spec);
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Page<Product> getAllProduct(Pageable able) {
        return productRepository.findAll(able);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public Product findByCode(String maSanPham) {
        return productRepository.findByCode(maSanPham);
    }

    @Override
    public void updateProductStatusBasedOnQuantity(Product product) {
        int totalQuantity = product.getProductDetails().stream()
                .mapToInt(pd -> Optional.ofNullable(pd.getQuantity()).orElse(0))
                .sum();

        int newStatus = totalQuantity == 0 ? 2 : 1;

        if (!Objects.equals(product.getStatus(), newStatus)) {
            product.setStatus(newStatus);
            try {
                save(product);
            } catch (IOException e) {
                e.printStackTrace(); // hoặc log ra nếu bạn có logger
            }
        }
    }

    @Override
    public ProductDto getProductDtoByCode(String code) {
        Product product = productRepository.findByCode(code);
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCreateDate(product.getCreateDate());
        dto.setUpdatedDate(product.getUpdatedDate());
        dto.setCategoryName(product.getCategory().getName());
        dto.setPriceMin(product.getPrice());

        // Convert danh sách ProductDetail → ProductDetailDto
        List<ProductDetailDto> detailDtos = product.getProductDetails().stream().map(detail -> {
            ProductDetailDto detailDto = new ProductDetailDto();
            detailDto.setId(detail.getId());
            detailDto.setPrice(detail.getPrice());
            detailDto.setQuantity(detail.getQuantity());
            detailDto.setProductId(product.getId());
            detailDto.setBarcode(detail.getBarcode());

            detailDto.setColorName(detail.getColor().getName());
            detailDto.setSizeName(detail.getSize().getName());

            detailDto.setProductName(product.getName());
            detailDto.setProductDescription(product.getDescription());
            detailDto.setBrandName(product.getBrand().getName());
            detailDto.setMaterialName(product.getMaterial().getName());
            detailDto.setCategoryName(product.getCategory().getName());

            // Convert ảnh
            List<ImageDto> imageDtos = detail.getImages().stream().map(img -> {
                ImageDto imageDto = new ImageDto();
                imageDto.setId(img.getId());
                imageDto.setLink(img.getLink());
                return imageDto;
            }).toList();

            detailDto.setImages(imageDtos);
            return detailDto;
        }).toList();

        dto.setProductDetailDtos(detailDtos);
        return dto;
    }

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCreateDate(product.getCreateDate());
        dto.setUpdatedDate(product.getUpdatedDate());

        // Gán list ProductDetailDto
        List<ProductDetailDto> detailDtos = product.getProductDetails().stream().map(detail -> {
            ProductDetailDto detailDto = new ProductDetailDto();
            detailDto.setId(detail.getId());
            detailDto.setQuantity(detail.getQuantity());
            detailDto.setPrice(detail.getPrice());
            detailDto.setBarcode(detail.getBarcode());
            detailDto.setProductId(product.getId());

            // Gán thêm Color ID để dùng lọc
            detailDto.setColorId(detail.getColor().getId());
            detailDto.setColorName(detail.getColor().getName());

            // Gán hình ảnh
            List<ImageDto> imageDtos = detail.getImages().stream().map(image -> {
                ImageDto imageDto = new ImageDto();
                imageDto.setId(image.getId());
                imageDto.setLink(image.getLink());
                return imageDto;
            }).collect(Collectors.toList());
            detailDto.setImages(imageDtos);

            return detailDto;
        }).collect(Collectors.toList());

        dto.setProductDetailDtos(detailDtos);

        return dto;
    }

}
