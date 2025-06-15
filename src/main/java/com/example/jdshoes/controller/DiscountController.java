package com.example.jdshoes.controller;

import com.example.jdshoes.dto.DiscountDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.repository.DiscountRepository;
import com.example.jdshoes.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DiscountController {

    @Autowired
    private  DiscountService discountService;

    @Autowired
    private DiscountRepository discountRepository;

    @GetMapping("/discounts")
    public String getAllDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        model.addAttribute("discount", new Discount());
        Pageable pageable = PageRequest.of(page, size);
        Page<Discount> discountPage = discountService.getAllDiscounts(pageable);

        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());

        return "FormQuanLyKhuyenMai"; // tên file .html trong thư mục templates
    }

    @PostMapping("/addDiscount")
    public String addDiscount(@Valid @ModelAttribute("discount") Discount discount,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        if (bindingResult.hasErrors()) {
            // Load lại phân trang cho danh sách
            Pageable pageable = PageRequest.of(page, size);
            Page<Discount> discountPage = discountService.getAllDiscounts(pageable);

            model.addAttribute("discountPage", discountPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", discountPage.getTotalPages());

            // Bắt buộc gán lại discount để giữ dữ liệu nhập
            model.addAttribute("discount", discount);
            // ➕ Thêm biến để hiển thị lại form khi có lỗi
            model.addAttribute("formHasError", true);

            return "FormQuanLyKhuyenMai";
        }

        discountService.createDiscount(discount);
        return "redirect:/discounts";
    }
    @PostMapping("/updateDiscount/{id}")
    public String updateDiscount(@Valid @ModelAttribute("discount") Discount discount,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        if (bindingResult.hasErrors()) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Discount> discountPage = discountService.getAllDiscounts(pageable);

            model.addAttribute("discountPage", discountPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", discountPage.getTotalPages());

            model.addAttribute("discount", discount);
            // ➕ Thêm biến để hiển thị lại form khi có lỗi
            model.addAttribute("formUpdateVisible", true);

            return "FormQuanLyKhuyenMai";
        }

        discountService.updateDiscount(discount);
        return "redirect:/discounts";
    }
    @GetMapping("/deleteDiscount/{id}")
    public String deleteDiscount(@PathVariable("id") Integer id) {
        discountService.deleteDiscount(id);
        return "redirect:/discounts";
    }
    @GetMapping("/detailDiscount/{id}")
    public String getDiscountById(@PathVariable("id") Integer id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  Model model) {
        Optional<Discount> optionalDiscount = discountService.getDiscountById(id);
        if (optionalDiscount.isPresent()) {
            model.addAttribute("discount", optionalDiscount.get());
        } else {
            return "redirect:/discounts";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Discount> discountPage = discountService.getAllDiscounts(pageable);

        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());
        model.addAttribute("formUpdateVisible", true);

        return "FormQuanLyKhuyenMai";
    }
    @GetMapping("/status/{id}")
    public String trangThai(@PathVariable("id") Integer id) {
        Discount discount = discountService.getDiscountById(id).orElse(null);
        if (discount != null) {
            Boolean currentStatus = discount.getStatus();
            discount.setStatus(!currentStatus);
            discountRepository.save(discount);
        }
        return "redirect:/discounts";
    }
    @GetMapping("/search")
    public String searchDiscount(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Gọi repo để lấy dữ liệu tìm kiếm theo trang
        List<Discount> result = discountRepository.findBySearchParams(
                keyword != null ? keyword.toLowerCase() : null,
                status,
                startDate,
                endDate
        );

        // Tạo danh sách phân trang giả lập nếu cần dùng Page
        Page<Discount> discountPage = new PageImpl<>(result, pageable, result.size());

        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());

        // ✅ Quan trọng: thêm discount vào model để tránh lỗi binding
        model.addAttribute("discount", new Discount());

        // Giữ lại thông tin lọc để hiển thị lại trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "FormQuanLyKhuyenMai";
    }


}
