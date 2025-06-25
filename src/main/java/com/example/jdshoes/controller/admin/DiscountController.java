package com.example.jdshoes.controller.admin;

//import com.example.jdshoes.dto.DiscountDto;
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


//        LocalDateTime now = LocalDateTime.now();
//        // Cập nhật trạng thái và lưu vào DB
//        List<Discount> discounts = discountPage.getContent();
//        for (Discount discount : discounts) {
//            boolean newStatus;
//
//            if (now.isBefore(discount.getStartDate())) {
//                newStatus = false; // chưa đến thời gian
//            } else if (now.isAfter(discount.getEndDate())) {
//                newStatus = false; // đã hết hạn
//            } else {
//                newStatus = true; // đang hoạt động
//            }
//            discount.setStatus(newStatus);
////            discountService.createDiscount(discount);
//        }

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
        // Tự sinh mã ngẫu nhiên, ví dụ: KM + 4 số random
        String maTuSinh = "KM" + (int)(Math.random() * 9000 + 1000);
        discount.setCode(maTuSinh);
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
        discount.setStatus(1);
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
//    @GetMapping("/admin/update-discount-status/{id}")
//    public String trangThai( RedirectAttributes redirectAttributes, @PathVariable("id") Integer id) {
//        Discount discount = discountRepository.findById(id).orElse(null);
//        if (discount != null) {
//            Boolean currentStatus = discount.getStatus();
//            discount.setStatus(!currentStatus);
//            try {
//                discountRepository.save(discount);
//                redirectAttributes.addFlashAttribute("message", "Mã giảm giá đã được cập nhật");
//            } catch (NotFoundException ex) {
//                redirectAttributes.addFlashAttribute("message", ex.getMessage());
//            }
//
//        }
//        return "redirect:/admin-only/discounts";
//    }
    @PostMapping("/admin/update-discount-status/{status}")
    public String updateDiscountCodeStatus(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("id") Integer id, @PathVariable int status) {
        try {
            discountService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá đã được cập nhật");
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin-only/discounts";
    }

}