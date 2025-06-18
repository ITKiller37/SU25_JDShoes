package com.example.jdshoes.controller.admin;

//import com.example.jdshoes.dto.DiscountDto;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DiscountController {

    @Autowired
    private  DiscountService discountService;

    @Autowired
    private DiscountRepository discountRepository;

    @GetMapping("/admin-only/discounts")
    public String getAllDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        model.addAttribute("discount", new Discount());
        Pageable pageable = PageRequest.of(page, size);
        Page<Discount> discountPage = discountService.getAllDiscounts(pageable);


        LocalDateTime now = LocalDateTime.now();
        // Cập nhật trạng thái và lưu vào DB
        List<Discount> discounts = discountPage.getContent();
        for (Discount discount : discounts) {
            boolean newStatus;

            if (now.isBefore(discount.getStartDate())) {
                newStatus = false; // chưa đến thời gian
            } else if (now.isAfter(discount.getEndDate())) {
                newStatus = false; // đã hết hạn
            } else {
                newStatus = true; // đang hoạt động
            }
            discount.setStatus(newStatus);
            discountService.createDiscount(discount);
        }

        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());

        return "admin/discount-code"; // tên file .html trong thư mục templates
    }
    @GetMapping("/admin-only/form-add-discount")
    public String formAddDiscount(Model model) {
        // Thêm một đối tượng Discount trống vào model với tên "discount"
        Discount newDiscount = new Discount();
        newDiscount.setType(1); // Mặc định là giảm theo phần trăm (Integer 1)
        model.addAttribute("discount", newDiscount);
        return "admin/discount-code-create";
    }
    @PostMapping("/admin-only/addDiscount")
    public String addDiscount(@Valid @ModelAttribute("discount") Discount discount,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // --- 1. Server-side Validation từ Bean Validation (@NotBlank, @NotNull) ---
        // Không còn lỗi cho trường 'code' nếu đã xóa @NotBlank trong Entity
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.err.println("Validation Error: " + error.getDefaultMessage()));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập.");
            model.addAttribute("discount", discount); // Giữ lại dữ liệu đã nhập
            return "admin/discount-code-create";
        }

        // --- 2. Logic nghiệp vụ Validation ---
        // Kiểm tra ngày bắt đầu phải trước ngày kết thúc
        if (discount.getStartDate() != null && discount.getEndDate() != null &&
                discount.getStartDate().isAfter(discount.getEndDate())) {
            bindingResult.rejectValue("startDate", "date.invalid", "Ngày bắt đầu phải trước ngày kết thúc.");
            model.addAttribute("errorMessage", "Ngày bắt đầu phải trước ngày kết thúc.");
            model.addAttribute("discount", discount); // Giữ lại dữ liệu đã nhập
            return "admin/discount-code-create";
        }

        // Đảm bảo chỉ có trường giảm giá đúng loại được lưu
        if (discount.getType() != null) {
            if (discount.getType() == 1) { // Giảm theo phần trăm (Integer 1)
                discount.setDiscountAmount(null); // Đảm bảo discountAmount là null nếu là phần trăm
                // Thêm validation nếu percentage là null khi type là 1
                if (discount.getPercentage() == null) {
                    bindingResult.rejectValue("percentage", "notnull", "Phần trăm giảm không được để trống.");
                    model.addAttribute("errorMessage", "Phần trăm giảm không được để trống.");
                    model.addAttribute("discount", discount);
                    return "admin/discount-code-create";
                }
            } else if (discount.getType() == 0) { // Giảm theo tiền mặt (Integer 0)
                discount.setPercentage(null); // Đảm bảo percentage là null nếu là tiền mặt
                // Thêm validation nếu discountAmount là null khi type là 0
                if (discount.getDiscountAmount() == null) {
                    bindingResult.rejectValue("discountAmount", "notnull", "Số tiền giảm không được để trống.");
                    model.addAttribute("errorMessage", "Số tiền giảm không được để trống.");
                    model.addAttribute("discount", discount);
                    return "admin/discount-code-create";
                }
            }
        } else {
            // Xử lý trường hợp type là null
            bindingResult.rejectValue("type", "type.required", "Vui lòng chọn phương thức giảm giá.");
            model.addAttribute("errorMessage", "Vui lòng chọn phương thức giảm giá.");
            model.addAttribute("discount", discount);
            return "admin/discount-code-create";
        }


        // --- 3. Thực hiện lưu vào Database ---
        try {
            discount.setStatus(false); // Mặc định trạng thái là hoạt động khi tạo mới
            discountService.createDiscount(discount);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mã giảm giá thành công!");
            return "redirect:/admin-only/discounts"; // Chuyển hướng đến trang danh sách
        } catch (Exception e) {
            System.err.println("Error creating discount: " + e.getMessage()); // In lỗi ra console để debug
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi thêm mã giảm giá: " + e.getMessage());
            model.addAttribute("discount", discount); // Giữ lại dữ liệu đã nhập
            return "admin/discount-code-create";
        }
    }
    @GetMapping("/admin-only/detailDiscount/{id}")
    public String getDiscountById(@PathVariable("id") Integer id, Model model) {
        Discount discount = discountService.getDiscountById(id).get();

        model.addAttribute("discount", discount);
        return "admin/discount-code-edit";
    }
    @PostMapping("/admin-only/updateDiscount/{id}")
    public String updateDiscount(Discount discount){
        discountService.updateDiscount(discount);
        return "redirect:/admin-only/discounts";
    }
    @GetMapping("/admin-only/searchDiscounts")
    public String listDiscounts(@RequestParam(value = "searchTerm", required = false) String searchTerm,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {

        model.addAttribute("discount", new Discount());

        Pageable pageable = PageRequest.of(page, size);
        Page<Discount> discountPage;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            discountPage = discountRepository.findBySingleSearchTermPaged(searchTerm.trim().toLowerCase(), pageable);
        } else {
            discountPage = discountRepository.findAll(pageable);
        }

        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());
        model.addAttribute("searchTerm", searchTerm);

        return "admin/discount-code";
    }



}