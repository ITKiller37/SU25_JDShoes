package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.service.BillService;
import com.example.jdshoes.service.ProductService;
import com.example.jdshoes.utils.UserLoginUtil;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserLoginUtil userLoginUtil;

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/bill-list")
    public String getBill(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "ngayTaoStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoStart,
            @RequestParam(name  = "ngayTaoEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoEnd,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(name = "loaiDon", required = false) String loaiDon
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
        if (keyword != null || ngayTaoStart != null || ngayTaoEnd != null || trangThai != null || loaiDon != null) {
            // Convert Date to LocalDateTime

            if(ngayTaoStart != null) {
                convertedNgayTaoStart = ngayTaoStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                model.addAttribute("ngayTaoStart", convertedNgayTaoStart.format(formatter));
            }
            if(ngayTaoEnd != null) {
                convertedNgayTaoEnd = ngayTaoEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                model.addAttribute("ngayTaoEnd", convertedNgayTaoEnd.format(formatter));
            }
            Bill = billService.searchListBill(keyword, convertedNgayTaoStart, convertedNgayTaoEnd, trangThai, loaiDon, pageable);
        } else {
            Bill = billService.findAll(pageable);
        }

        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("items", Bill);

        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("loaiDon", loaiDon);
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

    @GetMapping("/export-bill")
    public void exportBill(
            HttpServletResponse response,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField,
            @RequestParam(name = "ngayTaoStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoStart,
            @RequestParam(name  = "ngayTaoEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoEnd,
            UriComponentsBuilder uriBuilder
    ) throws IOException {
        int pageSize = 10;
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(sortDirection, sortFieldName);


        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<BillDtoInterface> bills;
        bills = billService.findAll(pageable);


        String exportUrl = uriBuilder.path("/export-bill")
                .queryParam("page", page)
                .queryParam("sort", sortField)
                .queryParam("ngayTaoStart", ngayTaoStart)
                .queryParam("ngayTaoEnd", ngayTaoEnd)
                .toUriString();

        billService.exportToExcel(response, bills, exportUrl);
    }

    @PostMapping("/update-bill-status/{billId}")
    public String updateBillStatus(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField, @PathVariable Long billId,
                                   @RequestParam String trangThaiDonHang, RedirectAttributes redirectAttributes,
                                   @RequestParam(required = false) String note
                                   ) {

        Account account = userLoginUtil.getCurrentLogin(); // Lấy Account từ UserLoginUtil
        if (account == null) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Người dùng chưa đăng nhập!");
            return "redirect:/admin/getbill-detail/" + billId;
        }

        try {
            Bill bill = billService.updateStatus(trangThaiDonHang, billId);
            billService.saveBillHistory(billId, BillStatus.valueOf(trangThaiDonHang), note, account);
            redirectAttributes.addFlashAttribute("message", "Hóa đơn " + bill.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật trạng thái!");
        }

        return "redirect:/admin/bill-list";
    }

    @PostMapping("/update-bill-status2/{billId}")
    public String updateBillStatus2(Model model, @PathVariable Long billId,
                                    @RequestParam String trangThaiDonHang, RedirectAttributes redirectAttributes,
                                    @RequestParam(required = false) String note
                                    ) {

        Account account = userLoginUtil.getCurrentLogin(); // Lấy Account từ UserLoginUtil
        if (account == null) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Người dùng chưa đăng nhập!");
            return "redirect:/admin/getbill-detail/" + billId;
        }
        try {
            Bill bill = billService.updateStatus(trangThaiDonHang, billId);
            billService.saveBillHistory(billId, BillStatus.valueOf(trangThaiDonHang), note, account);
            redirectAttributes.addFlashAttribute("message", "Hóa đơn " + bill.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật trạng thái!");
        }

        return "redirect:/admin/getbill-detail/" + billId ;
    }

    @GetMapping("/getbill-detail/{maHoaDon}")
    public String getBillDetail(Model model, @PathVariable("maHoaDon") Long maHoaDon) {

        BillDetailDtoInterface billDetailDtoInterface = billService.getBillDetail(maHoaDon);
        List<BillDetailProduct> billDetailProducts = billService.getBillDetailProduct(maHoaDon);
        List<BillHistoryDto> billHistory = billService.getBillHistory(maHoaDon);
        List<ProductDetail> products = productService.findAll();
        BigDecimal total = BigDecimal.ZERO; // Khởi tạo total là 0 với BigDecimal
        for (BillDetailProduct billDetailProduct : billDetailProducts) {
            int quantity = billDetailProduct.getSoLuong();
            BigDecimal price = billDetailProduct.getGiaTien(); // Giả sử getGiaTien trả về BigDecimal
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity)); // Tính giá tiền cho sản phẩm
            total = total.add(subtotal); // Cộng dồn vào tổng
        }

        // Lấy phí ship cũ từ BillHistory
        BigDecimal oldShippingFee = billService.getOldShippingFee(maHoaDon);
        if (oldShippingFee == null || oldShippingFee.compareTo(BigDecimal.ZERO) == 0) {
            // Nếu không tìm thấy phí ship cũ, giả định nó bằng phí ship hiện tại
            oldShippingFee = billDetailDtoInterface.getPhiShip() != null ? billDetailDtoInterface.getPhiShip() : BigDecimal.ZERO;
        }

        // Lấy tổng tiền sản phẩm cũ
        BigDecimal originalProductAmount = billService.getOriginalProductAmount(maHoaDon);

        model.addAttribute("billDetailProduct", billDetailProducts);
        model.addAttribute("billdetail", billDetailDtoInterface);
        model.addAttribute("bill", billService.findById(maHoaDon));
        model.addAttribute("billHistory", billHistory);
        model.addAttribute("products", products);
        model.addAttribute("total", total);
        model.addAttribute("oldShippingFee", oldShippingFee);
        model.addAttribute("originalProductAmount", originalProductAmount);
        return "admin/bill-detail";
    }

    @PostMapping("/add-product-to-bill/{billId}")
    public String addProductToBill(
            @PathVariable Long billId,
            @RequestParam Long productDetailId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            billService.addProductToBill(billId, productDetailId, quantity);
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi thêm sản phẩm!");
        }
        return "redirect:/admin/getbill-detail/" + billId;
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

    @PostMapping("/update-bill-address/{billId}")
    public String updateBillAddress(
            @PathVariable Long billId,
            @RequestParam String customerName,
            @RequestParam String customerPhoneNumber,
            @RequestParam String provinceId,
            @RequestParam String districtId,
            @RequestParam String wardCode,
            @RequestParam String customerAddress,
            @RequestParam BigDecimal shippingFee,
            RedirectAttributes redirectAttributes) {

        Account account = userLoginUtil.getCurrentLogin(); // Lấy Account từ UserLoginUtil
        if (account == null) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Người dùng chưa đăng nhập!");
            return "redirect:/admin/getbill-detail/" + billId;
        }
        try {
            // Cấu hình header cho API GHN
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", "6c4c54e4-5802-11f0-955f-6e01a39a71db");
            headers.set("Content-Type", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Lấy tên tỉnh/thành
            ResponseEntity<Map> provinceResponse = restTemplate.exchange(
                    "https://online-gateway.ghn.vn/shiip/public-api/master-data/province",
                    HttpMethod.GET, entity, Map.class);
            String provinceName = null;
            if (provinceResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> provinceData = provinceResponse.getBody();
                for (Map<String, Object> province : (Iterable<Map<String, Object>>) provinceData.get("data")) {
                    if (String.valueOf(province.get("ProvinceID")).equals(provinceId)) {
                        provinceName = (String) province.get("ProvinceName");
                        break;
                    }
                }
            }

            // Lấy tên quận/huyện
            ResponseEntity<Map> districtResponse = restTemplate.exchange(
                    "https://online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=" + provinceId,
                    HttpMethod.GET, entity, Map.class);
            String districtName = null;
            if (districtResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> districtData = districtResponse.getBody();
                for (Map<String, Object> district : (Iterable<Map<String, Object>>) districtData.get("data")) {
                    if (String.valueOf(district.get("DistrictID")).equals(districtId)) {
                        districtName = (String) district.get("DistrictName");
                        break;
                    }
                }
            }

            // Lấy tên phường/xã
            ResponseEntity<Map> wardResponse = restTemplate.exchange(
                    "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=" + districtId,
                    HttpMethod.GET, entity, Map.class);
            String wardName = null;
            if (wardResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> wardData = wardResponse.getBody();
                for (Map<String, Object> ward : (Iterable<Map<String, Object>>) wardData.get("data")) {
                    if (String.valueOf(ward.get("WardCode")).equals(wardCode)) {
                        wardName = (String) ward.get("WardName");
                        break;
                    }
                }
            }

            // Kiểm tra dữ liệu hợp lệ
            if (provinceName == null || districtName == null || wardName == null) {
                throw new Exception("Không thể lấy thông tin địa chỉ từ API GHN.");
            }

            // Tạo địa chỉ đầy đủ
            String fullAddress = String.format("%s, %s, %s, %s", customerAddress, wardName, districtName, provinceName);

            // Cập nhật hóa đơn
            Bill bill = billService.findById(billId);
            if (bill == null) {
                throw new Exception("Hóa đơn không tồn tại.");
            }

            // Lưu phí ship hiện tại vào BillHistory trước khi cập nhật
            if (bill.getShippingFee() != null) {
                String note = String.format("Cập nhật địa chỉ. Phí ship cũ: %s VNĐ", bill.getShippingFee());
                billService.saveBillHistory(billId, bill.getStatus(), note, account);
            }
            bill.setCustomerName(customerName);
            bill.setCustomerPhoneNumber(customerPhoneNumber);
            bill.setCustomerAddress(fullAddress);
            bill.setShippingFee(shippingFee);
            billService.save(bill);

            redirectAttributes.addFlashAttribute("message", "Cập nhật địa chỉ và phí ship thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật địa chỉ: " + e.getMessage());
        }
        return "redirect:/admin/getbill-detail/" + billId;
    }

//    @ResponseBody
//    @GetMapping("/api/order/status")
//    public ResponseEntity<Map<String, Object>> getOrderStatus(@RequestParam String orderCode) {
//        Map<String, Object> response = billService.getOrderStatus(orderCode);
//        if (response.get("order") != null) {
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//        }
//    }
}
