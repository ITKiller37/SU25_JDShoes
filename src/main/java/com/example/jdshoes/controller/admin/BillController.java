package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.service.BillService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class BillController {

    @Autowired
    private BillService billService;


    @GetMapping("/bill-list")
    public String getBill(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField,
            @RequestParam(name = "maDinhDanh", required = false) String maDinhDanh,
            @RequestParam(name = "ngayTaoStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoStart,
            @RequestParam(name  = "ngayTaoEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoEnd,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(name = "loaiDon", required = false) String loaiDon,
            @RequestParam(name = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(name = "hoVaTen", required = false) String hoVaTen
    ) {
        int pageSize = 8;
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(sortDirection, sortFieldName);
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<BillDtoInterface> Bill;
        LocalDateTime convertedNgayTaoStart = null;
        LocalDateTime convertedNgayTaoEnd = null;
        if (ngayTaoStart != null || ngayTaoEnd != null || maDinhDanh != null || trangThai != null || loaiDon != null || hoVaTen != null || soDienThoai != null) {
            // Convert Date to LocalDateTime

            if(ngayTaoStart != null) {
                convertedNgayTaoStart = ngayTaoStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                model.addAttribute("ngayTaoStart", convertedNgayTaoStart.format(formatter));
            }
            if(ngayTaoEnd != null) {
                convertedNgayTaoEnd = ngayTaoEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                model.addAttribute("ngayTaoEnd", convertedNgayTaoEnd.format(formatter));
            }
            Bill = billService.searchListBill(maDinhDanh.trim(), convertedNgayTaoStart, convertedNgayTaoEnd, trangThai, loaiDon, soDienThoai.trim(), hoVaTen.trim(), pageable);
        } else {
            Bill = billService.findAll(pageable);
        }

        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("items", Bill);

        model.addAttribute("maDinhDanh", maDinhDanh);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("loaiDon", loaiDon);
        model.addAttribute("soDienThoai", soDienThoai);
        model.addAttribute("hoVaTen", hoVaTen);
        model.addAttribute("sortField", sortField);
        model.addAttribute("billStatus", BillStatus.values());
        model.addAttribute("invoiceType", InvoiceType.values());
        return "admin/bill";
    }

    @GetMapping("/generate-pdf/{maHoaDon}")
    public ResponseEntity<String> generatePDF(@PathVariable Long maHoaDon) {
        try {
            String htmlContent = billService.getHtmlContent(maHoaDon);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html; charset=utf-8");
            return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Lỗi: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/export-pdf/{maHoaDon}")
    public String exportPdf(HttpServletResponse response, @PathVariable("maHoaDon") Long maHoaDon) throws DocumentException, IOException {
        return billService.exportPdf(response, maHoaDon);
    }

    @GetMapping("/update-bill-status/{billId}")
    public String updateBillStatus(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField, @PathVariable Long billId,
                                   @RequestParam String trangThaiDonHang, RedirectAttributes redirectAttributes) {
        try {
            Bill bill = billService.updateStatus(trangThaiDonHang, billId);
            redirectAttributes.addFlashAttribute("message", "Hóa đơn " + bill.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error updating status");
        }

        return "redirect:/admin/bill-list";
    }

    @GetMapping("/update-bill-status2/{billId}")
    public String updateBillStatus2(Model model, @PathVariable Long billId,
                                    @RequestParam String trangThaiDonHang, RedirectAttributes redirectAttributes) {
        try {
            Bill bill = billService.updateStatus(trangThaiDonHang, billId);
            redirectAttributes.addFlashAttribute("message", "Hóa đơn " + bill.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error updating status");
        }

        return "redirect:/admin/getbill-detail/" + billId ;
    }

    @GetMapping("/getbill-detail/{maHoaDon}")
    public String getBillDetail(Model model, @PathVariable("maHoaDon") Long maHoaDon) {

        BillDetailDtoInterface billDetailDtoInterface = billService.getBillDetail(maHoaDon);
        List<BillDetailProduct> billDetailProducts = billService.getBillDetailProduct(maHoaDon);
        BigDecimal total = BigDecimal.ZERO; // Khởi tạo total là 0 với BigDecimal
        for (BillDetailProduct billDetailProduct : billDetailProducts) {
            int quantity = billDetailProduct.getSoLuong();
            BigDecimal price = billDetailProduct.getGiaTien(); // Giả sử getGiaTien trả về BigDecimal
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity)); // Tính giá tiền cho sản phẩm
            total = total.add(subtotal); // Cộng dồn vào tổng
        }
        model.addAttribute("billDetailProduct", billDetailProducts);
        model.addAttribute("billdetail", billDetailDtoInterface);
        model.addAttribute("total", total);
        return "admin/bill-detail";
    }

    @ResponseBody
    @GetMapping("/api/product/{billId}/bill")
    public ResponseEntity<List<BillDetailProduct>> getAllProductByBillId(@PathVariable Long billId) {
        return ResponseEntity.ok(billService.getBillDetailProduct(billId));
    }

    @ResponseBody
    @GetMapping("/api/bill/validToReturn")
    public Page<BillDto> getAllValidBillToReturn(Pageable pageable) {
        return billService.getAllValidBillToReturn(pageable);
    }

    @ResponseBody
    @GetMapping("/api/bill/validToReturn/search")
    public Page<BillDto> getAllValidBillToReturnSearch(SearchBillDto searchBillDto, Pageable pageable) {
        return billService.searchBillJson(searchBillDto, pageable);
    }
}
