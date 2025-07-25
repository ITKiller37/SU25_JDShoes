package com.example.jdshoes.controller.admin;

//import com.example.jdshoes.dto.DiscountDto;
import com.example.jdshoes.dto.Discount.SearchDiscountCodeDto;
import com.example.jdshoes.entity.Discount;
import com.example.jdshoes.repository.DiscountRepository;
import com.example.jdshoes.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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

        // Kiểm tra nếu không nhập gì trong form tìm kiếm
        boolean isEmptySearch =
                (searchDto.getKeyword() == null || searchDto.getKeyword().isEmpty()) &&
                        searchDto.getStartDate() == null &&
                        searchDto.getEndDate() == null &&
                        searchDto.getStatus() == null &&
                        searchDto.getMaximumUsage() == null;

        Page<Discount> discountPage;

        if (isEmptySearch) {
            // Không tìm kiếm gì thì load mặc định theo ID giảm dần
            discountPage = discountRepository.findAllByOrderByIdDesc(pageable);
        } else {
            // Có tìm kiếm thì gọi service xử lý
            discountPage = discountService.searchDiscounts(searchDto, pageable);
        }

        model.addAttribute("discount", new Discount()); // để form thêm mới không lỗi
        model.addAttribute("discountPage", discountPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discountPage.getTotalPages());

        return "admin/discount-code"; // Trả về trang hiển thị
    }

    @GetMapping("/admin-only/form-add-discount")
    public String formAddDiscount(Model model) {
        // Thêm một đối tượng Discount trốg vào model với tên "discount"
        Discount newDiscount = new Discount();
        newDiscount.setDeleteFlag(true);
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
        discount.setDeleteFlag(true);
        discount.setStatus(1);
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
//        discount.setStatus(1);
        discountService.updateDiscount(discount);
        return "redirect:/admin-only/discounts";
    }

    @PostMapping("/admin-only/api/discounts/update-status")
    @ResponseBody
    public ResponseEntity<Void> updateStatus(@RequestBody Map<String, Object> payload) {
        Integer id = Integer.valueOf(payload.get("id").toString());
        int status = Integer.parseInt(payload.get("status").toString());

        Discount d = discountService.findById(id);
        if (d != null) {
            d.setStatus(status);
            discountService.createDiscount(d);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/admin-only/discount/change-deleteflag/{id}")
    public String changeDiscountStatus(@PathVariable("id") Integer id) {
        Discount discount = discountRepository.findById(id).orElse(null);
        if (discount != null) {
            discount.setDeleteFlag(!discount.isDeleteFlag()); // Đổi trạng thái
            discountRepository.save(discount);       // Lưu lại vào DB
        }
        return "redirect:/admin-only/discounts"; // Điều hướng lại trang danh sách
    }

    @Scheduled(fixedRate = 3000000) // 30 giây một lần
    public void updateDiscountStatusAutomatically() {
        List<Discount> discounts = discountRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Discount discount : discounts) {
            if (now.isBefore(discount.getStartDate())) {
                discount.setStatus(2); // Sắp diễn ra
            } else if (now.isAfter(discount.getEndDate())) {
                discount.setStatus(0); // Đã đóng
            } else {
                discount.setStatus(1); // Đang hoạt động
            }
            discountRepository.save(discount);
        }
    }

}