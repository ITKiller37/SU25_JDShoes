package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.*;
import com.example.jdshoes.repository.Specification.BillSpecification;
import com.example.jdshoes.service.BillService;
import com.example.jdshoes.utils.UserLoginUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private UserLoginUtil userLoginUtil;

    @Autowired
    private BillHistoryRepository billHistoryRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Override
    public Page<BillDtoInterface> searchListBill(String keyword,
                                                 LocalDateTime ngayTaoStart,
                                                 LocalDateTime ngayTaoEnd,
                                                 String trangThai,
                                                 String loaiDon,
                                                 Pageable pageable) {

        BillStatus status = null;
        InvoiceType invoiceType = null;

        try {
            status = BillStatus.valueOf(trangThai);
        } catch (IllegalArgumentException e) {

        }
        try {
            invoiceType = InvoiceType.valueOf(loaiDon);
        } catch (IllegalArgumentException e) {

        }
        return billRepository.listSearchBill(keyword,
                ngayTaoStart,
                ngayTaoEnd,
                status,
                invoiceType,
                pageable);
    }

    @Override
    public Page<BillDtoInterface> findAll(Pageable pageable) {
        return billRepository.listBill(pageable);
    }

    @Override
    public String getHtmlContent(Long maHoaDon) {
        BillDetailDtoInterface billDetailDtoInterface = billRepository.getBillDetail(maHoaDon);
        List<BillDetailProduct> billDetailProduct = billRepository.getBillDetailProduct(maHoaDon);
        Bill bill = billRepository.findById(maHoaDon)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn có mã " + maHoaDon));

        // Xử lý thông tin khách hàng
        String customerName;
        String customerPhone;
        String customerEmail;
        if (billDetailDtoInterface.getMaKhachHang() != null && !billDetailDtoInterface.getMaKhachHang().isEmpty()) {
            // Khách có tài khoản, lấy từ bảng Customer
            customerName = billDetailDtoInterface.getTenKhachTrongHeThong() != null ? billDetailDtoInterface.getTenKhachTrongHeThong() : "Khách lẻ";
            customerPhone = billDetailDtoInterface.getSoDienThoaiKhachHang() != null ? billDetailDtoInterface.getSoDienThoaiKhachHang() : "";
            customerEmail = billDetailDtoInterface.getEmail() != null ? billDetailDtoInterface.getEmail() : "";
        } else {
            // Khách lẻ, chỉ hiển thị tên là "Khách lẻ"
            customerName = "Khách lẻ";
            customerPhone = "";
            customerEmail = "";
        }

        // Xử lý thông tin người nhận (chỉ hiển thị nếu isShipping = true)
        String receiverInfo = "";
        if (bill.getIsShipping() != null && bill.getIsShipping()) {
            String receiverName = billDetailDtoInterface.getTenNguoiNhan() != null ? billDetailDtoInterface.getTenNguoiNhan() : "";
            String receiverPhone = billDetailDtoInterface.getSoDienThoaiNguoiNhan() != null ? billDetailDtoInterface.getSoDienThoaiNguoiNhan() : "";
            String address = billDetailDtoInterface.getDiaChiNguoiNhan() != null ? billDetailDtoInterface.getDiaChiNguoiNhan() : "";
            String shippingNote = billDetailDtoInterface.getGhiChuGiaoHang() != null ? billDetailDtoInterface.getGhiChuGiaoHang() : "";
            receiverInfo = "<div class='info-box'>" +
                    "<h4>Thông tin người nhận</h4>" +
                    "<p>Họ tên: " + receiverName + "</p>" +
                    "<p>SĐT: " + receiverPhone + "</p>" +
                    "<p>Địa chỉ: " + address + "</p>" +
                    "<p>Ghi chú: " + shippingNote + "</p>" +
                    "</div>";
        }

        // Xử lý các trường khác
        String orderStatus = billDetailDtoInterface.getTrangThaiDonHang() != null ? billDetailDtoInterface.getTrangThaiDonHang().toString() : "";
        String paymentMethod = billDetailDtoInterface.getPhuongThucThanhToan() != null ? billDetailDtoInterface.getPhuongThucThanhToan() : "";
        String transactionId = billDetailDtoInterface.getMaGiaoDich() != null ? billDetailDtoInterface.getMaGiaoDich() : "";
        String invoiceType = billDetailDtoInterface.getLoaiHoaDon() != null ? billDetailDtoInterface.getLoaiHoaDon().toString() : "";
        String voucherCode = billDetailDtoInterface.getVoucherName() != null ? billDetailDtoInterface.getVoucherName() : "";
        BigDecimal shippingFee = billDetailDtoInterface.getPhiShip() != null ? billDetailDtoInterface.getPhiShip() : BigDecimal.ZERO;

        // Lấy thông tin nhân viên từ UserLoginUtil
        Account currentAccount = userLoginUtil.getCurrentLogin();
        String employeeInfo = "";
        if (invoiceType.equals(InvoiceType.OFFLINE.toString()) && currentAccount != null) {
            String employeeName = currentAccount.getName() != null ? currentAccount.getName() : "Không xác định";
            String employeeId = currentAccount.getCode() != null ? currentAccount.getCode() : "N/A";
            employeeInfo = "<p><strong>Nhân viên tạo hóa đơn:</strong> " + employeeName + " (ID: " + employeeId + ")</p>";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Định dạng và tính tổng tiền
        BigDecimal totalMoney = BigDecimal.ZERO;
        Locale locale = new Locale("vi", "VN");
        DecimalFormat currencyFormatter = new DecimalFormat("#,### đ");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        currencyFormatter.setDecimalFormatSymbols(symbols);

        for (BillDetailProduct item : billDetailProduct) {
            totalMoney = totalMoney.add(item.getGiaTien().multiply(BigDecimal.valueOf(item.getSoLuong())));
        }

        // Tính tổng tiền thanh toán (bao gồm phí ship)
        BigDecimal finalTotal = totalMoney.subtract(billDetailDtoInterface.getTienKhuyenMai()).add(shippingFee);

        String htmlContent = "<html>" +
                "<head>" +
                "<meta charset='UTF-8' />" +
                "<title>Hóa đơn bán hàng</title>" +
                "<style>" +
                "body { font-family: 'SVN-Times New Roman', serif; margin: 30px; }" +
                "h1, h3 { text-align: center; margin: 4px 0; }" +
                ".header { text-align: center; margin-bottom: 20px; }" +
                ".info-section { display: flex; justify-content: space-between; margin-bottom: 10px; }" +
                ".info-box { width: 48%; font-size: 14px; line-height: 1.5; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px; }" +
                "th, td { border: 1px solid #000; padding: 8px; text-align: center; }" +
                "th { background-color: #f2f2f2; }" +
                ".total-section { margin-top: 15px; font-size: 16px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<img src='/admin/images/admin-3.png' height='100' style='float: left;'/>" +
                "<h1>HÓA ĐƠN BÁN HÀNG</h1>" +
                "<h3>Shop giày thể thao nam JDShoes</h3>" +
                "<h3>Hoàn kiếm, Hà Nội</h3>" +
                employeeInfo + // Thêm thông tin nhân viên vào header
                "</div>" +
                "<p><strong>Mã hóa đơn:</strong> " + billDetailDtoInterface.getMaDinhDanh() + "</p>" +
                "<div class='info-section'>" +
                "<div class='info-box'>" +
                "<h4>Thông tin khách hàng</h4>" +
                "<p>Họ tên: " + customerName + "</p>" +
                "<p>SĐT: " + customerPhone + "</p>" +
                "<p>Email: " + customerEmail + "</p>" +
                "</div>" +
                receiverInfo + // Chỉ hiển thị nếu isShipping = true
                "</div>" +
                "<p><strong>Ngày tạo:</strong> " + billDetailDtoInterface.getCreatedDate().format(formatter) + "</p>" +
                "<p><strong>Phương thức thanh toán:</strong> " + paymentMethod + "</p>" +
                "<p><strong>Mã voucher:</strong> " + voucherCode + "</p>" +
                "<h3>Danh sách sản phẩm</h3>" +
                "<table>" +
                "<tr><th>Tên sản phẩm</th><th>Màu sắc</th><th>Kích cỡ</th><th>Giá</th><th>Số lượng</th><th>Thành tiền</th></tr>";

        for (BillDetailProduct item : billDetailProduct) {
            htmlContent += "<tr>" +
                    "<td>" + item.getTenSanPham() + "</td>" +
                    "<td>" + item.getTenMau() + "</td>" +
                    "<td>" + item.getKichCo() + "</td>" +
                    "<td>" + currencyFormatter.format(item.getGiaTien()) + "</td>" +
                    "<td>" + item.getSoLuong() + "</td>" +
                    "<td>" + currencyFormatter.format(item.getTongTien()) + "</td>" +
                    "</tr>";
        }

        htmlContent += "</table>" +
                "<div class='total-section'>" +
                "<p><strong>Tổng tiền sản phẩm:</strong> " + currencyFormatter.format(totalMoney) + "</p>" +
                "<p><strong>Tiền giảm giá:</strong> " + currencyFormatter.format(billDetailDtoInterface.getTienKhuyenMai()) + "</p>" +
                "<p><strong>Phí ship:</strong> " + currencyFormatter.format(shippingFee) + "</p>" +
                "<h4><strong>Thành tiền phải thanh toán:</strong> " + currencyFormatter.format(finalTotal) + "</h4>" +
                "</div>" +
                "</body></html>";
        return htmlContent;
    }

    @Override
    public String exportPdf(HttpServletResponse response, Long maHoaDon) throws DocumentException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=hoa-don-" + maHoaDon + ".pdf");

        ITextRenderer renderer = new ITextRenderer();

        // Thêm font hỗ trợ tiếng Việt
        String fontPath = "/static/fonts/time-new-roman/SVN-Times New Roman.ttf"; // Đảm bảo đường dẫn đúng
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        String htmlContent = getHtmlContent(maHoaDon);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        try (OutputStream outputStream = response.getOutputStream()) {
            renderer.createPDF(outputStream);
            renderer.finishPDF();
        } catch (Exception e) {
            throw new ShoesApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi tạo PDF: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Bill updateStatus(String trangThaiDonHang, Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bill có mã" + billId));

        BillStatus oldStatus = bill.getStatus();
        BillStatus newStatus = BillStatus.valueOf(trangThaiDonHang);

        // Kiểm tra tính hợp lệ của chuyển trạng thái
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                    "Chuyển trạng thái từ " + oldStatus + " sang " + newStatus + " không hợp lệ.");
        }

        // Nếu hủy đơn
        if(newStatus == BillStatus.HUY) {
            // Chỉ cộng lại số lượng nếu đơn đã từng được xác nhận (đã trừ số lượng)
            if(oldStatus == BillStatus.DA_XAC_NHAN) {
                List<BillDetailProduct> billDetailProducts = billRepository.getBillDetailProduct(billId);
                billDetailProducts.forEach(item -> {
                    ProductDetail productDetail = productDetailRepository.findById(item.getId())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy thuộc tính " + item.getId()));
                    productDetail.setQuantity(productDetail.getQuantity() + item.getSoLuong());
                    productDetailRepository.save(productDetail);
                });
            }
        }
        // Nếu chuyển sang trạng thái DA_XAC_NHAN (từ trạng thái ban đầu)
        else if(newStatus == BillStatus.DA_XAC_NHAN && oldStatus == BillStatus.CHO_XAC_NHAN) {
            List<BillDetailProduct> billDetailProducts = billRepository.getBillDetailProduct(billId);
            for (BillDetailProduct item : billDetailProducts) {
                ProductDetail productDetail = productDetailRepository.findById(item.getId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy thuộc tính " + item.getId()));

                if(productDetail.getQuantity() < item.getSoLuong()) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm, không đủ để xác nhận đơn hàng",
                                    productDetail.getProduct().getName(),
                                    productDetail.getSize().getName(),
                                    productDetail.getColor().getName(),
                                    productDetail.getQuantity()));
                }

                productDetail.setQuantity(productDetail.getQuantity() - item.getSoLuong());
                productDetailRepository.save(productDetail);

            }
        }

        bill.setStatus(newStatus);
        bill.setUpdateDate(LocalDateTime.now());
        return billRepository.save(bill);
    }

    // Phương thức hỗ trợ để kiểm tra chuyển trạng thái hợp lệ
    private boolean isValidStatusTransition(BillStatus oldStatus, BillStatus newStatus) {
        // Chỉ cho phép hủy khi trạng thái hiện tại là CHO_XAC_NHAN hoặc DA_XAC_NHAN
        if (newStatus == BillStatus.HUY) {
            return oldStatus == BillStatus.CHO_XAC_NHAN || oldStatus == BillStatus.DA_XAC_NHAN || oldStatus == BillStatus.CHO_LAY_HANG;
        }
        // Chỉ cho phép xác nhận từ CHO_XAC_NHAN sang DA_XAC_NHAN
        else if (newStatus == BillStatus.DA_XAC_NHAN) {
            return oldStatus == BillStatus.CHO_XAC_NHAN;
        }
        // Cho phép chuyển từ DA_XAC_NHAN sang CHO_LAY_HANG
        else if (newStatus == BillStatus.CHO_LAY_HANG) {
            return oldStatus == BillStatus.DA_XAC_NHAN;
        }
        // Cho phép chuyển từ CHO_LAY_HANG sang DANG_GIAO_HANG
        else if (newStatus == BillStatus.DANG_GIAO_HANG) {
            return oldStatus == BillStatus.CHO_LAY_HANG;
        }
        // Cho phép chuyển từ DANG_GIAO_HANG sang GIAO_HANG_THANH_CONG hoặc GIAO_HANG_THAT_BAI
        else if (newStatus == BillStatus.GIAO_HANG_THANH_CONG || newStatus == BillStatus.GIAO_HANG_THAT_BAI) {
            return oldStatus == BillStatus.DANG_GIAO_HANG;
        }
        // Cho phép chuyển từ GIAO_HANG_THANH_CONG sang HOAN_THANH hoặc TRA_HANG
        else if (newStatus == BillStatus.HOAN_THANH || newStatus == BillStatus.TRA_HANG) {
            return oldStatus == BillStatus.GIAO_HANG_THANH_CONG;
        }

        // Không cho phép các chuyển trạng thái khác (có thể mở rộng nếu cần)
        return false;
    }

    @Override
    public BillDetailDtoInterface getBillDetail(Long maHoaDon) {
        return billRepository.getBillDetail(maHoaDon);
    }

    @Override
    public List<BillDetailProduct> getBillDetailProduct(Long maHoaDon) {
        return billRepository.getBillDetailProduct(maHoaDon);
    }

    @Override
    public Page<BillDto> searchBillJson(SearchBillDto searchBillDto, Pageable pageable) {
        Specification<Bill> spec = new BillSpecification(searchBillDto);
        Page<Bill> bills = billRepository.findAll(spec, pageable);
        return bills.map(this::convertToDto);
    }

    private BillDto convertToDto(Bill bill) {
        BillDto billDto = new BillDto();
        billDto.setId(bill.getId());
        billDto.setCode(bill.getCode());
        billDto.setCreateDate(bill.getCreateDate());
        billDto.setStatus(bill.getStatus());
        billDto.setUpdateDate(bill.getUpdateDate());

        CustomerDto customer = new CustomerDto();
        if (bill.getCustomer() != null) {
            customer.setName(bill.getCustomer().getName());
            customer.setId(bill.getCustomer().getId());
            customer.setCode(bill.getCustomer().getCode());
            // Dòng setCode bị trùng, có thể bỏ 1 dòng nếu không cần
        }
        billDto.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;
        for (BillDetail billDetail : bill.getBillDetail()) {
            BigDecimal lineTotal = billDetail.getDetailPrice()
                    .multiply(BigDecimal.valueOf(billDetail.getQuantity()));
            total = total.add(lineTotal);
        }

        billDto.setTotalAmount(total); // Lưu ý: field totalAmount nên là BigDecimal
        return billDto;
    }

    @Override
    public Page<BillDto> getAllValidBillToReturn(Pageable pageable) {
        return billRepository.findValidBillToReturn(pageable).map(this::convertToDto);
    }

    @Override
    public List<BillDetailProduct> getBillDetailProductBill(Long maHoaDon) {
        return billRepository.getBillDetailProductBill(maHoaDon);
    }

    @Override
    public Map<String, Object> getOrderStatus(String orderCode) {
        Map<String, Object> response = new HashMap<>();
        Bill bill = billRepository.findByCode(orderCode);
        if (bill != null) {
            Long maHoaDon = bill.getId();
            BillDetailDtoInterface billDetail = billRepository.getBillDetail(maHoaDon);
            List<BillDetailProduct> products = billRepository.getBillDetailProduct(maHoaDon);

            // Tính tổng tiền sản phẩm
            BigDecimal total = products.stream()
                    .map(product -> product.getTongTien() != null ? product.getTongTien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("order", billDetail);
            response.put("products", products);
            response.put("total", total);
        } else {
            response.put("order", null);
            response.put("products", null);
            response.put("total", BigDecimal.ZERO);
        }
        return response;
    }

    @Override
    public Page<Bill> getBillByAccount(Pageable pageable) {
        Account account = userLoginUtil.getCurrentLogin();
        return billRepository.findByCustomer_Account_Id(account.getId(), pageable);
    }

    @Override
    public Page<Bill> getBillByStatus(String status, Pageable pageable) {
        Account account = userLoginUtil.getCurrentLogin();
        BillStatus status1 = null;

        try {
            status1 = BillStatus.valueOf(status);
        } catch (IllegalArgumentException e) {

        }
        return billRepository.findAllByStatusAndCustomer_Account_Id(status1, account.getId(), pageable);
    }

    @Override
    public void saveBillHistory(Long billId, BillStatus billStatus, String note, Account account) {
        BillHistory billHistory = new BillHistory();
        billHistory.setBill(billRepository.findById(billId).orElseThrow());
        billHistory.setStatus(billStatus);
        billHistory.setNote(note);
        billHistory.setCreatedAt(LocalDateTime.now());
        billHistory.setCreatedBy(account);
        billHistoryRepository.save(billHistory);
        billHistoryRepository.flush();
    }

    @Override
    public List<BillHistoryDto> getBillHistory(Long maHoaDon) {
        return billRepository.getBillHistory(maHoaDon);
    }

    @Override
    public Bill findById(Long billId) {
        return billRepository.findById(billId).orElse(null);
    }

    @Override
    public void save(Bill bill) {
        billRepository.save(bill);
    }

    @Override
    @Transactional
    public void addProductToBill(Long billId, Long productDetailId, Integer quantity) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn có mã " + billId));
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm có mã " + productDetailId));

        // Kiểm tra trạng thái hóa đơn
        if (bill.getStatus() == BillStatus.DANG_GIAO_HANG ||
                bill.getStatus() == BillStatus.GIAO_HANG_THANH_CONG ||
                bill.getStatus() == BillStatus.HOAN_THANH ||
                bill.getStatus() == BillStatus.HUY ||
                bill.getStatus() == BillStatus.TRA_HANG ||
                bill.getStatus() == BillStatus.GIAO_HANG_THAT_BAI) {
            throw new IllegalStateException("Không thể thêm sản phẩm vào hóa đơn ở trạng thái hiện tại!");
        }

        // Kiểm tra số lượng tồn kho
        if (productDetail.getQuantity() < quantity) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                    String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm, không đủ để thêm vào đơn hàng",
                            productDetail.getProduct().getName(),
                            productDetail.getSize().getName(),
                            productDetail.getColor().getName(),
                            productDetail.getQuantity()));
        }

        // Tính giá sản phẩm (ưu tiên giá khuyến mãi nếu có)
        ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
        BigDecimal detailPrice = productDiscount != null ? productDiscount.getDiscountedAmount() : productDetail.getPrice();
        BigDecimal newProductAmount = detailPrice.multiply(new BigDecimal(quantity));

        // Tạo BillDetail mới
        BillDetail billDetail = new BillDetail();
        billDetail.setBill(bill);
        billDetail.setProductDetail(productDetail);
        billDetail.setQuantity(quantity);
        billDetail.setDetailPrice(detailPrice);
        billDetailRepository.save(billDetail);

        // Cập nhật số lượng tồn kho
        productDetail.setQuantity(productDetail.getQuantity() - quantity);
        productDetailRepository.save(productDetail);

        // Cập nhật tổng tiền hóa đơn
        BigDecimal currentTotal = bill.getAmount() != null ? bill.getAmount() : BigDecimal.ZERO;
        bill.setAmount(currentTotal.add(newProductAmount));
        bill.setUpdateDate(LocalDateTime.now());
        billRepository.save(bill);

        // Lưu lịch sử thêm sản phẩm
        Account account = userLoginUtil.getCurrentLogin();
        String note = String.format("Thêm sản phẩm %s - Số lượng: %d - Giá: %s - Tổng tiền sản phẩm mới: %s",
                productDetail.getProduct().getName(),
                quantity,
                NumberFormat.getNumberInstance(Locale.US).format(detailPrice),
                NumberFormat.getNumberInstance(Locale.US).format(newProductAmount));
        saveBillHistory(billId, bill.getStatus(), note, account);

        // Nếu hóa đơn đã thanh toán (tiền mặt hoặc chuyển khoản), lưu lịch sử số tiền cần thanh toán thêm
        if (bill.getPaymentMethod().getId() == 1L || bill.getPaymentMethod().getId() == 3L) {
            BigDecimal originalProductAmount = getOriginalProductAmount(billId);
            BigDecimal oldShippingFee = getOldShippingFee(billId);
            BigDecimal newProductsAmount = bill.getAmount().subtract(originalProductAmount);
            BigDecimal shippingDiff = bill.getShippingFee() != null ? bill.getShippingFee().subtract(oldShippingFee) : BigDecimal.ZERO;
            BigDecimal additionalAmount = (newProductsAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newProductsAmount)
                    .add(shippingDiff.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : shippingDiff);

            String additionalNote = String.format("Số tiền cần thanh toán thêm: %s VNĐ (Sản phẩm mới: %s VNĐ, Chênh lệch phí ship: %s VNĐ)",
                    NumberFormat.getNumberInstance(Locale.US).format(additionalAmount),
                    NumberFormat.getNumberInstance(Locale.US).format(newProductsAmount),
                    NumberFormat.getNumberInstance(Locale.US).format(shippingDiff));
            saveBillHistory(billId, bill.getStatus(), additionalNote, account);
        }
    }

    @Override
    public BigDecimal getOldShippingFee(Long maHoaDon) {
        List<BillHistory> historyList = billHistoryRepository.findByBillIdOrderByCreatedAtAsc(maHoaDon);
        for (BillHistory history : historyList) {
            if (history.getNote() != null) {
                // Ưu tiên ghi chú cập nhật địa chỉ
                if (history.getNote().startsWith("Cập nhật địa chỉ. Phí ship cũ:")) {
                    try {
                        String note = history.getNote();
                        String feeString = note.replace("Cập nhật địa chỉ. Phí ship cũ: ", "").replace(" VNĐ", "");
                        return new BigDecimal(feeString.replace(",", ""));
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi phân tích phí ship từ ghi chú: " + history.getNote());
                    }
                }
                // Nếu không có ghi chú cập nhật địa chỉ, lấy từ ghi chú tạo hóa đơn
                else if (history.getNote().startsWith("Tạo hóa đơn") && history.getNote().contains("Phí ship ban đầu:")) {
                    try {
                        String note = history.getNote();
                        String feeString = note.substring(note.indexOf("Phí ship ban đầu: ") + 18).replace(" VNĐ", "");
                        return new BigDecimal(feeString.replace(",", ""));
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi phân tích phí ship từ ghi chú: " + history.getNote());
                    }
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getOriginalProductAmount(Long maHoaDon) {
        List<BillHistory> historyList = billHistoryRepository.findByBillIdOrderByCreatedAtAsc(maHoaDon);
        for (BillHistory history : historyList) {
            if (history.getNote() != null && history.getNote().contains("Tổng tiền sản phẩm ban đầu")) {
                try {
                    String note = history.getNote();
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Tổng tiền sản phẩm ban đầu: (\\d{1,3}(?:,\\d{3})*)");
                    java.util.regex.Matcher matcher = pattern.matcher(note);
                    if (matcher.find()) {
                        String amountString = matcher.group(1).replaceAll(",", "");
                        System.out.println("Ghi chú: " + note + ", Số tiền trích xuất: " + amountString);
                        if (!amountString.isEmpty()) {
                            return new BigDecimal(amountString);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi phân tích số tiền từ ghi chú: " + history.getNote());
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Không tìm thấy ghi chú tổng tiền sản phẩm ban đầu cho hóa đơn: " + maHoaDon);
        return BigDecimal.ZERO;
    }

    @Override
    public void exportToExcel(HttpServletResponse response, Page<BillDtoInterface> bills, String exportUrl) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bills.xlsx");

        // Tạo workbook Excel và các sheet, row, cell tương ứng
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Get the current date in the "dd-MM-yyyy" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        // Create a sheet with the name "bill_dd-mm-yyyy"
        XSSFSheet sheet = workbook.createSheet("bill_" + currentDate);

        // Tạo tiêu đề cột
        XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Hóa Đơn");
        headerRow.createCell(1).setCellValue("Họ và Tên");
        headerRow.createCell(2).setCellValue("Số điện thoại");
        headerRow.createCell(3).setCellValue("Ngày đặt");
        headerRow.createCell(4).setCellValue("Tổng tiền");
        headerRow.createCell(5).setCellValue("Trạng thái");
        headerRow.createCell(6).setCellValue("Loại đơn");
        headerRow.createCell(7).setCellValue("Hình thực thanh toán");

        int rowNum = 1;
        for (BillDtoInterface bill : bills) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bill.getMaDinhDanh());
            row.createCell(1).setCellValue(bill.getHoVaTen());
            row.createCell(2).setCellValue(bill.getSoDienThoai());
            XSSFCell dateCell = row.createCell(3);
            XSSFCellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat dataFormat = workbook.createDataFormat();
            dateCellStyle.setDataFormat(dataFormat.getFormat("dd/MM/yyyy"));
            dateCell.setCellStyle(dateCellStyle);
            dateCell.setCellValue(bill.getNgayTao());
            XSSFCell totalCell = row.createCell(4);
            totalCell.setCellValue(bill.getTongTien());

            XSSFCellStyle totalCellStyle = workbook.createCellStyle();
            XSSFDataFormat dataFormat1 = workbook.createDataFormat();
            totalCellStyle.setDataFormat(dataFormat1.getFormat("#,###"));
            totalCell.setCellStyle(totalCellStyle);
            String trangThaiText = "";
            switch (bill.getTrangThai()) {
                case CHO_XAC_NHAN:
                    trangThaiText = "Chờ xác nhận";
                    break;
                case DA_XAC_NHAN:
                    trangThaiText = "Đã xác nhận";
                    break;
                case CHO_LAY_HANG:
                    trangThaiText = "Chờ lấy hàng";
                    break;
                case DANG_GIAO_HANG:
                    trangThaiText = "Đang giao hàng";
                    break;
                case GIAO_HANG_THANH_CONG:
                    trangThaiText = "Giao hàng thành công";
                    break;
                case GIAO_HANG_THAT_BAI:
                    trangThaiText = "Giao hàng thất bại";
                    break;
                case HOAN_THANH:
                    trangThaiText = "Hoàn thành";
                    break;
                case HUY:
                    trangThaiText = "Hủy";
                    break;
                case TRA_HANG:
                    trangThaiText = "Trả hàng";
                    break;
                default:
                    trangThaiText = "";
            }
            String loaiDonText = "";
            switch (bill.getLoaiDon()) {
                case OFFLINE:
                    loaiDonText = "Tại quầy";
                    break;
                case ONLINE:
                    loaiDonText = "Trực tuyến";
                    break;
                default:
                    loaiDonText = "  ";
            }
            row.createCell(5).setCellValue(trangThaiText);
            row.createCell(6).setCellValue(loaiDonText);
            row.createCell(7).setCellValue(bill.getHinhThucThanhToan());

            XSSFCell linkCell = row.createCell(8);
            XSSFRichTextString linkText = new XSSFRichTextString(" ");
            CreationHelper createHelper = workbook.getCreationHelper();
            XSSFHyperlink hyperlink = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(exportUrl);
            linkCell.setHyperlink(hyperlink);
            linkCell.setCellValue(linkText);
        }

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
        }
    }

}
