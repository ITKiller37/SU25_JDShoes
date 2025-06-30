package com.example.jdshoes.controller.admin;

//import com.example.jdshoes.dto.DiscountDto;
import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.repository.DiscountRepository;
import com.example.jdshoes.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DiscountController {

    @Autowired
    private  DiscountService discountService;

    @Autowired
    private DiscountRepository discountRepository;

    @GetMapping("/admin-only/discounts")
    public String searchDiscounts(
            @ModelAttribute("dataSearch") SearchDiscountCodeDto searchDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Gọi service để tìm kiếm theo điều kiện trong DTO
        Page<Discount> discountPage = discountService.searchDiscounts(searchDto, pageable);

        model.addAttribute("discount", new Discount()); // để form thêm mới không lỗi
        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());

        return "admin/discount-code"; // trả về view
    }

    @GetMapping("/admin-only/form-add-discount")
    public String formAddDiscount(Model model) {
        // Thêm một đối tượng Discount trốg vào model với tên "discount"
        Discount newDiscount = new Discount();
        newDiscount.setDeleteFlag(false);
        newDiscount.setType(1); // Mặc định là giảm theo phần trăm (Integer 1)
        model.addAttribute("discount", newDiscount);
        return "admin/discount-code-create";
    }
    @PostMapping("/admin-only/addDiscount")
    public String addDiscount(@Valid @ModelAttribute("discount") Discount discount,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        // Tự sinh mã ngẫu nhiên, ví dụ: KM + 4 số random
        String maTuSinh = "KM" + (int)(Math.random() * 9000 + 1000);
        discount.setCode(maTuSinh);
        discount.setStatus(0);
        discountService.createDiscount(discount);
        return "redirect:/admin-only/discounts";
    }
    @GetMapping("/admin-only/detailDiscount/{id}")
    public String getDiscountById(@PathVariable("id") Integer id, Model model) {
        Discount discount = discountService.getDiscountById(id).get();

        model.addAttribute("discount", discount);
        return "admin/discount-code-edit";
    }
    @PostMapping("/admin-only/updateDiscount/{id}")
    public String updateDiscount(Discount discount){
        discount.setStatus(1);
        discountService.updateDiscount(discount);
        return "redirect:/admin-only/discounts";
    }
    @GetMapping("/admin-only/discount-statuses")
    @ResponseBody
    public List<Map<String, Object>> getDiscountStatuses() {
        List<Discount> list = discountService.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Discount discount : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", discount.getId());
            item.put("status", discount.getStatus());
            result.add(item);
        }
        return result;
    }
    @PostMapping("/admin-only/change-discount-status")
    public String changeDiscountStatus(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Discount discount = discountService.findById(id); // đảm bảo bạn có phương thức này
            if (discount == null) {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy mã giảm giá!");
                return "redirect:/admin-only/discounts";
            }

            int currentStatus = discount.getStatus();
            int newStatus = switch (currentStatus) {
                case 2 -> 1; // Sắp diễn ra → Hoạt động
                case 1 -> 0; // Hoạt động → Đã đóng
                case 0 -> 2; // Đã đóng → Sắp diễn ra
                default -> 2; // fallback nếu status sai
            };

            discount.setStatus(newStatus);
            discountService.createDiscount(discount); // hoặc updateDiscount(discount)
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật trạng thái: " + ex.getMessage());
        }

        return "redirect:/admin-only/discounts";
    }


}