
package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.ProductDiscount.CreateProductDiscountRequest;
import com.example.jdshoes.dto.ProductDiscount.DiscountedProductDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.entity.ProductDiscountDetail;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductDiscountDetailRepository;
import com.example.jdshoes.repository.ProductDiscountRepository;
import com.example.jdshoes.service.ProductDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductDiscountDetailRepository productDiscountDetailRepository;


    @Autowired
    private ProductDiscountRepository discountRepo;

    @Autowired
    private ProductDiscountDetailRepository detailRepo;

    @Autowired
    private ProductDetailRepository productDetailRepo;

    @Override
    public List<ProductDiscountDto> getAllDiscounts() {
        List<ProductDiscount> discounts = productDiscountRepository.findAll();
        return discounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ProductDiscountDto convertToDto(ProductDiscount discount) {
        ProductDiscountDto dto = new ProductDiscountDto();
        dto.setId(discount.getId());
        dto.setCode(discount.getCode());
        dto.setName(discount.getName());
        dto.setDescription(discount.getDescription());
        dto.setDiscountedAmount(discount.getDiscountedAmount());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        dto.setType(discount.getType());
        discount.setStatus(discount.getStatus());


        // Tính trạng thái theo ngày hiện tại
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = discount.getStartDate();
        LocalDateTime end = discount.getEndDate();

        if (start == null || end == null) {
            dto.setStatus("Không xác định");
        } else if (now.isBefore(start)) {
            dto.setStatus("Chưa bắt đầu");
        } else if (now.isAfter(end)) {
            dto.setStatus("Đã kết thúc");
        } else {
            dto.setStatus("Đang hoạt động");
        }

        return dto;
    }


    @Override
    public void deleteById(Integer id) {
        ProductDiscount productDiscount = productDiscountRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt giảm giá có id = " + id));
        productDiscountRepository.delete(productDiscount);
    }

    @Override
    public ProductDiscount createDiscount(ProductDiscountDto dto) {
        ProductDiscount discount = new ProductDiscount();
        discount.setCode(generateCode());
        discount.setName(dto.getName());
        discount.setType(dto.getType());

        // Gán trực tiếp discountedAmount làm value
        discount.setValue(dto.getDiscountedAmount());

        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discount.setClosed(false);
        discount.setDescription(dto.getDescription());

        // Logic xác định status theo ngày hiện tại
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(dto.getStartDate())) {
            discount.setStatus("Sắp diễn ra");
        } else if (now.isAfter(dto.getEndDate())) {
            discount.setStatus("Đã kết thúc");
        } else {
            discount.setStatus("Đang diễn ra");
        }

        return productDiscountRepository.save(discount);
    }

    private String generateCode() {
        return "DIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @Override
    public void createMultipleDiscounts(CreateProductDiscountRequest request) {
        ProductDiscount discount = new ProductDiscount();
        discount.setCode(request.getCode());
        discount.setName(request.getName());
        discount.setValue(request.getValue());
        discount.setType(request.getType());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setDescription(request.getDescription());
        discount.setDiscountedAmount(request.getDiscountedAmount());

        // ✅ Bổ sung: set status theo thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(request.getStartDate())) {
            discount.setStatus("Chưa bắt đầu");
        } else if (now.isAfter(request.getEndDate())) {
            discount.setStatus("Đã kết thúc");
        } else {
            discount.setStatus("Đang hoạt động");
        }

        // Lưu đợt giảm giá trước
        ProductDiscount savedDiscount = productDiscountRepository.save(discount);

        // Lấy danh sách sản phẩm chi tiết
        List<ProductDetail> productDetails = productDetailRepository.findAllById(request.getProductDetailIds());

        // Khởi tạo nếu cần
        if (savedDiscount.getProductDiscountDetails() == null) {
            savedDiscount.setProductDiscountDetails(new ArrayList<>());
        }

        for (ProductDetail detail : productDetails) {
            ProductDiscountDetail discountDetail = new ProductDiscountDetail();
            discountDetail.setProductDiscount(savedDiscount);
            discountDetail.setProductDetail(detail);
            savedDiscount.getProductDiscountDetails().add(discountDetail);
        }

        productDiscountRepository.save(savedDiscount);
    }


    public ProductDiscountDto getById(Long id) {
        ProductDiscount discount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt giảm giá"));

        return convertToDto(discount);
    }


    @Override
    public List<DiscountedProductDto> getDiscountedProductDtosByDiscountId(Long discountId) {
        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt giảm giá"));

        List<ProductDiscountDetail> discountDetails = productDiscountDetailRepository.findAllByProductDiscountId(discountId);

        return discountDetails.stream().map(detail -> {
            ProductDetail pd = detail.getProductDetail();

            DiscountedProductDto dto = new DiscountedProductDto();
            dto.setProductDetailId(pd.getId());
            dto.setProductName(pd.getProduct().getName());
            dto.setSizeName(pd.getSize().getName());
            dto.setColorName(pd.getColor().getName());
            dto.setBarcode(pd.getBarcode());
            dto.setOriginalPrice(pd.getPrice());
            dto.setQuantity(pd.getQuantity());
            BigDecimal discountedPrice = pd.getPrice();
            if ("%".equals(discount.getType())) {
                discountedPrice = pd.getPrice().subtract(
                        pd.getPrice().multiply(discount.getValue().divide(BigDecimal.valueOf(100)))
                );
            } else {
                discountedPrice = pd.getPrice().subtract(discount.getValue());
            }

            dto.setDiscountedPrice(discountedPrice);
            dto.setDiscountType(discount.getType());
            dto.setDiscountValue(discount.getValue());
            dto.setDiscountCode(discount.getCode());
            dto.setDiscountName(discount.getName());

            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public void updateDiscount(Long id, CreateProductDiscountRequest request) {
        ProductDiscount discount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá"));

        discount.setName(request.getName());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setDescription(request.getDescription());
        discount.setDiscountedAmount(request.getDiscountedAmount());
        discount.setCode(request.getCode());

        // Cập nhật danh sách sản phẩm áp dụng
        discount.getProductDiscountDetails().clear();

        List<ProductDiscountDetail> newDetails = request.getProductDetailIds().stream()
                .map(detailId -> {
                    ProductDiscountDetail detail = new ProductDiscountDetail();
                    detail.setProductDiscount(discount);
                    detail.setProductDetail(productDetailRepository.findById(detailId).orElseThrow());
                    return detail;
                }).toList();

        discount.getProductDiscountDetails().addAll(newDetails);

        productDiscountRepository.save(discount);
    }


}


