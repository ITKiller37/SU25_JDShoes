package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Discount.DiscountDto;
import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscountRestController {

    private final DiscountService discountService;

    @ResponseBody
    @GetMapping("/api/private/discount-code")
    public Page<DiscountDto> getAllDiscountCodes(SearchDiscountCodeDto searchDiscountCodeDto, Pageable pageable) {
        return discountService.getAllDiscountCode(searchDiscountCodeDto, pageable);
    }

    @ResponseBody
    @GetMapping("/api/private/discount-code-valid")
    public Page<DiscountDto> getAllValidDiscountCodes(Pageable pageable) {
        return discountService.getAllAvailableDiscountCode(pageable);
    }
}
