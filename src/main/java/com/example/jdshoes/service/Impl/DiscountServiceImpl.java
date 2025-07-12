package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Discount.DiscountDto;
import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.DiscountRepository;
import com.example.jdshoes.repository.Specification.DiscountCodeSpec;
import com.example.jdshoes.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public Page<Discount> getAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable);
    }

    @Override
    public Optional<Discount> getDiscountById(Integer id){
        return discountRepository.findById(id);
    }
    @Override
    public Page<Discount> searchDiscounts(SearchDiscountCodeDto searchDto, Pageable pageable) {
        return discountRepository.findDiscountsByFilter(
                searchDto.getCode(),
                searchDto.getStartDate(),
                searchDto.getEndDate(),
                searchDto.getStatus(),
                searchDto.getMaximumUsage(),
                pageable
        );
    }
    @Override
    public Discount createDiscount (Discount discount){
        return discountRepository.save(discount);
    }

    public Discount findById(Integer id) {
        return discountRepository.findById(id).orElse(null);
    }

    @Override
    public Page<DiscountDto> getAllDiscountCode(SearchDiscountCodeDto searchDiscountCodeDto, Pageable pageable) {
        Specification<Discount> spec = new DiscountCodeSpec(searchDiscountCodeDto);
        Page<Discount> discountCodes = discountRepository.findAll(spec, pageable);
        return discountCodes.map(this::convertToDto);
    }

    @Override
    public Page<DiscountDto> getAllAvailableDiscountCode(Pageable pageable) {
        Page<Discount> validCodes = discountRepository.findAllAvailableValid(pageable);
        return validCodes.map(this::convertToDto);
    }


    @Override
    public void updateDiscount(Discount discountInput) {
        // Lấy entity cũ từ DB
        Discount existingDiscount = discountRepository.findById(discountInput.getId()).orElse(null);
        if (existingDiscount != null) {
            // KHÔNG cập nhật code (giữ nguyên)
            // existingDiscount.setCode(...); --> BỎ

            // Cập nhật các trường còn lại
            existingDiscount.setName(discountInput.getName());
            existingDiscount.setType(discountInput.getType());
            existingDiscount.setStatus(discountInput.getStatus());
            existingDiscount.setStartDate(discountInput.getStartDate());
            existingDiscount.setEndDate(discountInput.getEndDate());
            existingDiscount.setDiscountAmount(discountInput.getDiscountAmount());
            existingDiscount.setPercentage(discountInput.getPercentage());
            existingDiscount.setMaximumAmount(discountInput.getMaximumAmount());
            existingDiscount.setMaximumUsage(discountInput.getMaximumUsage());
            existingDiscount.setMinimumAmount(discountInput.getMinimumAmount());
            existingDiscount.setNote(discountInput.getNote());

            discountRepository.save(existingDiscount);
        }
    }

    //    @Override
//    public Discount updateStatus(Integer discountCodeId, int status) {
//        Discount discountCode = discountRepository.findById(discountCodeId).orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá"));
//        discountCode.setStatus(status);
//        return convertToDto(discountRepository.save(discountCode));
//    }
    private DiscountDto convertToDto(Discount discountCode) {
        DiscountDto dto = new DiscountDto();
        dto.setId(discountCode.getId());
        dto.setCode(discountCode.getCode().trim());
        dto.setName(discountCode.getName().trim());
        dto.setDiscountAmount(discountCode.getDiscountAmount());
        dto.setMaximumAmount(discountCode.getMaximumAmount());
        dto.setMinimumAmount(discountCode.getMinimumAmount());
        dto.setPercentage(discountCode.getPercentage());
        dto.setStartDate(discountCode.getStartDate());
        dto.setEndDate(discountCode.getEndDate());
        dto.setType(discountCode.getType());
        dto.setMaximumUsage(discountCode.getMaximumUsage());
        dto.setDeleteFlag(discountCode.isDeleteFlag());
        dto.setStatus(discountCode.getStatus());
        dto.setNote(discountCode.getNote());
        return dto;
    }


}