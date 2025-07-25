package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.BillDetail;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.BillRepository;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.Specification.BillSpecification;
import com.example.jdshoes.service.BillService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public Page<BillDtoInterface> searchListBill(String maDinhDanh,
                                                 LocalDateTime ngayTaoStart,
                                                 LocalDateTime ngayTaoEnd,
                                                 String trangThai,
                                                 String loaiDon,
                                                 String soDienThoai,
                                                 String hoVaTen,
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
        return billRepository.listSearchBill(maDinhDanh,
                ngayTaoStart,
                ngayTaoEnd,
                status,
                invoiceType,
                soDienThoai,
                hoVaTen,
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

        // Xử lý null cho các trường
        String email = billDetailDtoInterface.getEmail() != null ? billDetailDtoInterface.getEmail() : "";
        String customerName = billDetailDtoInterface.getTenKhachHang() != null ? billDetailDtoInterface.getTenKhachHang() : "";
        String receiverName = billDetailDtoInterface.getTenNguoiNhan() != null ? billDetailDtoInterface.getTenNguoiNhan() : "";
        String customerPhone = billDetailDtoInterface.getSoDienThoaiKhachHang() != null ? billDetailDtoInterface.getSoDienThoaiKhachHang() : "";
        String receiverPhone = billDetailDtoInterface.getSoDienThoaiNguoiNhan() != null ? billDetailDtoInterface.getSoDienThoaiNguoiNhan() : "";
        String address = billDetailDtoInterface.getDiaChiNguoiNhan() != null ? billDetailDtoInterface.getDiaChiNguoiNhan() : "";
        String shippingNote = billDetailDtoInterface.getGhiChuGiaoHang() != null ? billDetailDtoInterface.getGhiChuGiaoHang() : "";
        String orderStatus = billDetailDtoInterface.getTrangThaiDonHang() != null ? billDetailDtoInterface.getTrangThaiDonHang().toString() : "";
        String paymentMethod = billDetailDtoInterface.getPhuongThucThanhToan() != null ? billDetailDtoInterface.getPhuongThucThanhToan() : "";
        String transactionId = billDetailDtoInterface.getMaGiaoDich() != null ? billDetailDtoInterface.getMaGiaoDich() : "";
        String invoiceType = billDetailDtoInterface.getLoaiHoaDon() != null ? billDetailDtoInterface.getLoaiHoaDon().toString() : "";
        String voucherCode = billDetailDtoInterface.getVoucherName() != null ? billDetailDtoInterface.getVoucherName() : "";
        BigDecimal shippingFee = billDetailDtoInterface.getPhiShip() != null ? billDetailDtoInterface.getPhiShip() : BigDecimal.ZERO;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Định dạng và tính tổng tiền
        BigDecimal totalMoney = BigDecimal.ZERO;
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        for (BillDetailProduct item : billDetailProduct) {
            totalMoney = totalMoney.add(item.getGiaTien().multiply(BigDecimal.valueOf(item.getSoLuong())));
        }

        // Tính tổng tiền thanh toán (bao gồm phí ship)
        BigDecimal finalTotal = totalMoney.subtract(billDetailDtoInterface.getTienKhuyenMai()).add(shippingFee);

        String htmlContent = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>Hóa đơn bán hàng</title>" +
                "<style>" +
                "body { font-family: 'Times New Roman', serif; margin: 30px; }" +
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
                "<img src='/admin/images/admin-2.jpg' height='100' style='float: left;'/>" +
                "<h1>HÓA ĐƠN BÁN HÀNG</h1>" +
                "<h3>Shop giày thể thao nam JDShoes</h3>" +
                "<h3>Hoàn kiếm, Hà Nội</h3>" +
                "</div>" +

                "<p><strong>Mã hóa đơn:</strong> " + billDetailDtoInterface.getMaDinhDanh() + "</p>" +
                "<div class='info-section'>" +
                "<div class='info-box'>" +
                "<h4>Thông tin khách hàng</h4>" +
                "<p>Họ tên: " + customerName + "</p>" +
                "<p>SĐT: " + customerPhone + "</p>" +
                "<p>Email: " + email + "</p>" +
                "</div>" +

                "<div class='info-box'>" +
                "<h4>Thông tin người nhận</h4>" +
                "<p>Họ tên: " + receiverName + "</p>" +
                "<p>SĐT: " + receiverPhone + "</p>\n" +
                "<p>Địa chỉ: " + address + "</p>" +
                "<p>Ghi chú: " + shippingNote + "</p>" +
                "</div>" +
                "</div>" +

                "<p><strong>Ngày tạo:</strong> " + billDetailDtoInterface.getCreatedDate().format(formatter) + "</p>" +
                "<p><strong>Trạng thái đơn hàng:</strong> " + orderStatus + "</p>" +
                "<p><strong>Phương thức thanh toán:</strong> " + paymentMethod + "</p>" +
                "<p><strong>Mã giao dịch:</strong> " + transactionId + "</p>" +
                "<p><strong>Loại hóa đơn:</strong> " + invoiceType + "</p>" +
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
        return "";
    }

    @Override
    public Bill updateStatus(String trangThaiDonHang, Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bill có mã" + billId));

        BillStatus oldStatus = bill.getStatus();
        BillStatus newStatus = BillStatus.valueOf(trangThaiDonHang);

        // Nếu hủy đơn
        if(newStatus == BillStatus.HUY) {
            // Chỉ cộng lại số lượng nếu đơn đã từng được xác nhận (đã trừ số lượng)
            if(oldStatus != BillStatus.CHO_XAC_NHAN) {
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
}
