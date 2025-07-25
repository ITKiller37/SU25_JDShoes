package com.example.jdshoes.controller.admin;


import com.example.jdshoes.dto.BillReturn.*;
import com.example.jdshoes.repository.BillDetailRepository;
import com.example.jdshoes.repository.BillRepository;
import com.example.jdshoes.service.BillReturnService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class BillReturnController {
    private final BillReturnService billReturnService;
    private final BillDetailRepository billDetailRepository;

    public BillReturnController(BillReturnService billReturnService, BillRepository billRepository, BillDetailRepository billDetailRepository) {
        this.billReturnService = billReturnService;
        this.billDetailRepository = billDetailRepository;
    }

    @GetMapping("/admin-only/bill-return")
    public String viewBillReturnPage(SearchBillReturnDto searchBillReturnDto, Model model) {
        List<BillReturnDto> billReturnList = billReturnService.getAllBillReturns(searchBillReturnDto);
        model.addAttribute("returnList", billReturnList);
        return "admin/bill-return";
    }

    @GetMapping("/admin-only/bill-return-create")
    public String viewBillReturnCreatePage(Model model) {

        return "admin/bill-return-create";
    }

    @GetMapping("/admin-only/bill-return-detail/{id}")
    public String viewBillReturnDetailPage(Model model, @PathVariable Long id) {
        BillReturnDetailDto billReturnDetailDto = billReturnService.getBillReturnDetailById(id);

        BigDecimal total = BigDecimal.ZERO;
        for (RefundProductDto refundProductDto : billReturnDetailDto.getRefundProductDtos()) {
            BigDecimal price = Optional.ofNullable(refundProductDto.getDetailPrice()).orElse(BigDecimal.ZERO);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(refundProductDto.getQuantityRefund()));
            total = total.add(lineTotal);
        }

        BigDecimal totalReturn = BigDecimal.ZERO;
        for (ReturnProductDto returnProductDto : billReturnDetailDto.getReturnProductDtos()) {
            BigDecimal price = Optional.ofNullable(returnProductDto.getDetailPrice()).orElse(BigDecimal.ZERO);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(returnProductDto.getQuantityReturn()));
            totalReturn = totalReturn.add(lineTotal);
        }

        model.addAttribute("total", total);
        model.addAttribute("totalReturn", totalReturn);
        model.addAttribute("billReturnDetail", billReturnDetailDto);

        return "admin/bill-return-detail";
    }



    @GetMapping("/admin-only/bill-return-detail-code/{code}")
    public String viewBillReturnDetailPageByCode(Model model, @PathVariable String code) {
        BillReturnDetailDto billReturnDetailDto = billReturnService.getBillReturnDetailByCode(code);

        BigDecimal total = BigDecimal.ZERO;
        for (RefundProductDto refundProductDto : billReturnDetailDto.getRefundProductDtos()) {
            BigDecimal price = refundProductDto.getDetailPrice() != null
                    ? refundProductDto.getDetailPrice()
                    : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(refundProductDto.getQuantityRefund()));
            total = total.add(lineTotal);
        }

        BigDecimal totalReturn = BigDecimal.ZERO;
        for (ReturnProductDto returnProductDto : billReturnDetailDto.getReturnProductDtos()) {
            BigDecimal price = returnProductDto.getDetailPrice() != null
                    ? returnProductDto.getDetailPrice()
                    : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(returnProductDto.getQuantityReturn()));
            totalReturn = totalReturn.add(lineTotal);
        }

        model.addAttribute("total", total);
        model.addAttribute("totalReturn", totalReturn);
        model.addAttribute("billReturnDetail", billReturnDetailDto);

        return "admin/bill-return-detail";
    }


    @GetMapping("/admin/bill-return-detail-generate/{id}")
    public String generateHtmlPrint(Model model, @PathVariable Long id) {
        BillReturnDetailDto billReturnDetailDto = billReturnService.getBillReturnDetailById(id);

        BigDecimal total = BigDecimal.ZERO;

        for (RefundProductDto refundProductDto : billReturnDetailDto.getRefundProductDtos()) {
            BigDecimal price = refundProductDto.getDetailPrice() != null
                    ? refundProductDto.getDetailPrice()
                    : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(refundProductDto.getQuantityRefund()));
            total = total.add(lineTotal);
        }

        BigDecimal totalReturn = BigDecimal.ZERO;

        for (ReturnProductDto returnProductDto : billReturnDetailDto.getReturnProductDtos()) {
            BigDecimal price = returnProductDto.getDetailPrice() != null
                    ? returnProductDto.getDetailPrice()
                    : BigDecimal.ZERO;
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(returnProductDto.getQuantityReturn()));
            totalReturn = totalReturn.add(lineTotal);
        }

        model.addAttribute("total", total);
        model.addAttribute("totalReturn", totalReturn);
        model.addAttribute("billReturnDetail", billReturnDetailDto);

        return "admin/invoice-return-print";
    }


    @PostMapping("/admin/update-bill-return-status")
    public String updateBillReturnStatus(@ModelAttribute("billReturnDto") BillReturnDto billReturnDto, Model model, RedirectAttributes redirectAttributes) {

        try {
            BillReturnDto updatedBillReturn = billReturnService.updateStatus(billReturnDto.getId(), billReturnDto.getReturnStatus());
            redirectAttributes.addFlashAttribute("message", "Đơn đổi trả " + updatedBillReturn.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            model.addAttribute("message", "Error updating status");
        }
        return "redirect:/admin-only/bill-return";
    }

    @ResponseBody
    @PostMapping("/api/bill-return")
    public BillReturnDto createBillReturn(@RequestBody BillReturnCreateDto billReturnCreateDto) {
        return billReturnService.createBillReturn(billReturnCreateDto);
    }
}
