
package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.ProductDiscount.CreateProductDiscountRequest;
import com.example.jdshoes.dto.ProductDiscount.DiscountedProductDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDetailDto;
import com.example.jdshoes.dto.ProductDiscount.ProductDiscountDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        dto.setValue(discount.getValue());
        dto.setOriginalValue(discount.getOriginalValue());
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
    public void deleteById(Long id) {
        ProductDiscount productDiscount = productDiscountRepository.findById(id)
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
        discount.setType(request.getType());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setDescription(request.getDescription());
        discount.setClosed(false);

        // 👇 Thêm 3 dòng này
        discount.setValue(request.getValue());
        discount.setOriginalValue(request.getOriginalValue());
        discount.setDiscountedAmount(request.getDiscountedAmount());

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(request.getStartDate())) {
            discount.setStatus("Sắp diễn ra");
        } else if (now.isAfter(request.getEndDate())) {
            discount.setStatus("Đã kết thúc");
        } else {
            discount.setStatus("Đang diễn ra");
        }

        // Lưu discount
        ProductDiscount savedDiscount = productDiscountRepository.save(discount);

        // Lưu chi tiết giảm giá cho từng sản phẩm
        for (ProductDiscountDetailDto detailDto : request.getProductDiscounts()) {
            ProductDetail productDetail = productDetailRepository.findById(detailDto.getProductDetailId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy ProductDetail"));

            ProductDiscountDetail discountDetail = new ProductDiscountDetail();
            discountDetail.setProductDiscount(savedDiscount);
            discountDetail.setProductDetail(productDetail);
            discountDetail.setDiscountedAmount(detailDto.getDiscountedAmount());

            productDiscountDetailRepository.save(discountDetail);
        }
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
//            dto.setOriginalPrice(pd.getO());
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
        discount.setCode(request.getCode());
        discount.setOriginalValue(request.getOriginalValue());

        // Cập nhật danh sách sản phẩm chi tiết (xóa cũ, thêm mới)
        if (discount.getProductDiscountDetails() != null) {
            discount.getProductDiscountDetails().clear();
        } else {
            discount.setProductDiscountDetails(new ArrayList<>());
        }

        List<ProductDiscountDetail> newDetails = request.getProductDiscounts().stream()
                .map(dto -> {
                    ProductDetail productDetail = productDetailRepository.findById(dto.getProductDetailId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Không tìm thấy sản phẩm chi tiết ID: " + dto.getProductDetailId()
                            ));
                    ProductDiscountDetail detail = new ProductDiscountDetail();
                    detail.setProductDiscount(discount);
                    detail.setProductDetail(productDetail);
                    detail.setDiscountedAmount(dto.getDiscountedAmount()); // 💥 cần dòng này
                    return detail;
                }).toList();

        discount.getProductDiscountDetails().addAll(newDetails);

        productDiscountRepository.save(discount);
    }



}


