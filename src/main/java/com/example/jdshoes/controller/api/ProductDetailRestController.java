package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.service.ImageService;
import com.example.jdshoes.service.ProductDetailService;
import com.example.jdshoes.service.ProductService;
import com.example.jdshoes.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/product-details")
public class ProductDetailRestController {

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductService productService;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @GetMapping("/{id}")
    public ProductDetailDto getProductDetailById(@PathVariable Long id) {
        Optional<ProductDetail> productDetailOptional = productDetailService.findById(id);

        if (productDetailOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Detail not found");
        }
        ProductDetail productDetail = productDetailOptional.get();

        // Chuyển đổi sang DTO
        ProductDetailDto dto = new ProductDetailDto();
        dto.setId(productDetail.getId());
        dto.setQuantity(productDetail.getQuantity());
        dto.setPrice(productDetail.getPrice());
        dto.setBarcode(productDetail.getBarcode());
        dto.setProductId(productDetail.getProduct() != null ? productDetail.getProduct().getId() : null);

        // Lấy tên màu và kích cỡ
        if (productDetail.getColor() != null) {
            dto.setColorName(productDetail.getColor().getName());
        }
        if (productDetail.getSize() != null) {
            dto.setSizeName(productDetail.getSize().getName());
        }

        // BẮT ĐẦU TRÍCH XUẤT THÔNG TIN TỪ ĐỐI TƯỢNG PRODUCT LIÊN QUAN
        if (productDetail.getProduct() != null) {
            Product product = productDetail.getProduct(); // Lấy đối tượng Product
            dto.setProductName(product.getName());
            dto.setProductDescription(product.getDescription());

            if (product.getBrand() != null) {
                dto.setBrandName(product.getBrand().getName());
            }
            if (product.getMaterial() != null) {
                dto.setMaterialName(product.getMaterial().getName());
            }
            if (product.getCategory() != null) {
                dto.setCategoryName(product.getCategory().getName());
            }
        }
        // KẾT THÚC TRÍCH XUẤT THÔNG TIN TỪ ĐỐI TƯỢNG PRODUCT LIÊN QUAN

        // Chuyển đổi danh sách ảnh
        if (productDetail.getImages() != null) {
            dto.setImages(productDetail.getImages().stream().map(image -> {
                ImageDto imageDto = new ImageDto();
                imageDto.setId(image.getId());
                imageDto.setLink(image.getLink());
                return imageDto;
            }).toList());
        }

        return dto;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProductDetail(
            @PathVariable Long id,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description, // Thêm tham số description
            @RequestParam(value = "imagesToDelete", required = false) List<Long> imagesToDelete,
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages) {
        try {
            Optional<ProductDetail> optionalProductDetail = productDetailService.findById(id);
            if (optionalProductDetail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Detail not found.");
            }

            ProductDetail productDetail = optionalProductDetail.get();

            // 1. Cập nhật thông tin cơ bản của ProductDetail
            productDetail.setQuantity(quantity);
            productDetail.setPrice(price);

            // 2. Cập nhật description của Product (áp dụng chung cho tất cả biến thể)
            if (productDetail.getProduct() != null) {
                Product product = productDetail.getProduct();
                product.setDescription(description); // Cập nhật description của Product
                // Lưu Product (có thể cần service để xử lý transaction)
                // Giả sử bạn có ProductService, nếu không, có thể gọi productDetailService.save(productDetail) để cascade
            }

            // 3. Xóa ảnh cũ
            if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                for (Long imageId : imagesToDelete) {
                    Optional<Image> optionalImage = imageService.findById(imageId);
                    if (optionalImage.isPresent()) {
                        Image imageToDelete = optionalImage.get();
                        try {
                            Path filePath = Paths.get(uploadDirectory, imageToDelete.getName());
                            Files.deleteIfExists(filePath);
                            System.out.println("Deleted physical file: " + filePath.toString());
                        } catch (IOException e) {
                            System.err.println("Could not delete physical image file: " + imageToDelete.getName() + ". Error: " + e.getMessage());
                        }
                        imageService.delete(imageId);
                        productDetail.getImages().removeIf(img -> img.getId().equals(imageId));
                    }
                }
            }

            // 4. Thêm ảnh mới
            if (newImages != null && !newImages.isEmpty()) {
                List<Image> currentImages = productDetail.getImages();
                if (currentImages == null) {
                    currentImages = new ArrayList<>();
                    productDetail.setImages(currentImages);
                }
                for (MultipartFile file : newImages) {
                    if (!file.isEmpty()) {
                        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                        String storedFileName = FileUploadUtil.saveFile(uploadDirectory, originalFileName, file);
                        Image newImage = new Image();
                        newImage.setName(storedFileName);
                        newImage.setCreateDate(LocalDateTime.now());
                        newImage.setUpdateDate(LocalDateTime.now());
                        newImage.setLink("uploads/" + storedFileName);
                        newImage.setFileType(file.getContentType());
                        newImage.setProductDetail(productDetail);
                        currentImages.add(newImage);
                    }
                }
            }

            // Lưu ProductDetail (và cascade đến Product nếu có cấu hình)
            productDetailService.save(productDetail);
            // Cập nhật trạng thái sản phẩm dựa vào tổng số lượng
            if (productDetail.getProduct() != null) {
                productService.updateProductStatusBasedOnQuantity(productDetail.getProduct());
            }

            return ResponseEntity.ok("Product Detail and Product description updated successfully.");

        } catch (IOException e) {
            System.err.println("File upload/delete error during update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image files during update: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error updating product detail: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}