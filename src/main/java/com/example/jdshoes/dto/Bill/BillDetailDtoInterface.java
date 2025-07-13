package com.example.jdshoes.dto.Bill;

import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BillDetailDtoInterface {

    String getMaDonHang();

    String getMaDinhDanh();

    String getDiaChiNguoiNhan();

    BigDecimal getTongTien();

    BigDecimal getTienKhuyenMai();

    BigDecimal getPhiShip();

    String getTenKhachHang();

    String getTenNguoiNhan();

    String getSoDienThoaiKhachHang();

    String getSoDienThoaiNguoiNhan();

    String getGhiChuGiaoHang();

    String getEmail();

    BillStatus getTrangThaiDonHang();

    String getPhuongThucThanhToan();

    String getMaGiaoDich();

    InvoiceType getLoaiHoaDon();

    String getVoucherName();

    LocalDateTime getCreatedDate();
}
