package com.example.jdshoes.repository;

import com.example.jdshoes.dto.Bill.BillDetailDtoInterface;
import com.example.jdshoes.dto.Bill.BillDetailProduct;
import com.example.jdshoes.dto.Bill.BillDtoInterface;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> , JpaSpecificationExecutor<Bill> {

    @Query(value = "SELECT DISTINCT b.id AS maHoaDon, b.code AS maDinhDanh, " +
            "COALESCE(b.customerName, a.name, 'Khách lẻ') AS hoVaTen, " +
            "COALESCE(b.customerPhoneNumber, a.phoneNumber, '') AS soDienThoai, " +
            "b.createDate AS ngayTao, b.amount AS tongTien, b.status AS trangThai, b.invoiceType AS loaiDon, " +
            "pm.name AS hinhThucThanhToan, COALESCE(br.code, '') AS maDoiTra " +
            "FROM Bill b " +
            "LEFT JOIN Customer a ON b.customer.id = a.id " +
            "LEFT JOIN BillDetail bd ON b.id = bd.bill.id " +
            "LEFT JOIN PaymentMethod pm ON b.paymentMethod.id = pm.id " +
            "LEFT JOIN BillExchange br ON b.id = br.bill.id " +
            "WHERE (:maDinhDanh IS NULL OR b.code LIKE CONCAT('%', :maDinhDanh, '%')) " +
            "AND (:ngayTaoStart IS NULL OR :ngayTaoEnd IS NULL OR (b.createDate BETWEEN :ngayTaoStart AND :ngayTaoEnd)) " +
            "AND (:trangThai IS NULL OR b.status = :trangThai) " +
            "AND (:loaiDon IS NULL OR b.invoiceType = :loaiDon) " +
            "AND (:soDienThoai IS NULL OR b.customerPhoneNumber LIKE CONCAT('%', :soDienThoai, '%') OR a.phoneNumber LIKE CONCAT('%', :soDienThoai, '%')) " +
            "AND (:hoVaTen IS NULL OR b.customerName LIKE CONCAT('%', :hoVaTen, '%') OR a.name LIKE CONCAT('%', :hoVaTen, '%'))")
    Page<BillDtoInterface> listSearchBill(
                                          @Param("maDinhDanh") String maDinhDanh,
                                          @Param("ngayTaoStart") LocalDateTime ngayTaoStart,
                                          @Param("ngayTaoEnd") LocalDateTime ngayTaoEnd,
                                          @Param("trangThai") BillStatus trangThai,
                                          @Param("loaiDon") InvoiceType loaiDon,
                                          @Param("soDienThoai") String soDienThoai,
                                          @Param("hoVaTen") String hoVaTen,
                                          Pageable pageable);

    @Query(value = "SELECT DISTINCT b.id AS maHoaDon, b.code AS maDinhDanh, " +
            "COALESCE(b.customerName, a.name, 'Khách lẻ') AS hoVaTen, " +
            "COALESCE(b.customerPhoneNumber, a.phoneNumber, '') AS soDienThoai, " +
            "b.createDate AS ngayTao, b.amount AS tongTien, b.status AS trangThai, b.invoiceType AS loaiDon, " +
            "pm.name AS hinhThucThanhToan, COALESCE(br.code, '') AS maDoiTra, pmt.orderId AS maGiaoDich " +
            "FROM Bill b " +
            "JOIN Payment pmt ON b.id = pmt.bill.id " +
            "LEFT JOIN Customer a ON b.customer.id = a.id " +
            "LEFT JOIN BillDetail bd ON b.id = bd.bill.id " +
            "LEFT JOIN PaymentMethod pm ON b.paymentMethod.id = pm.id " +
            "LEFT JOIN BillExchange br ON b.id = br.bill.id")
    Page<BillDtoInterface> listBill(Pageable pageable);

    @Query(value = "SELECT b.id AS maDonHang, b.code AS maDinhDanh, " +
            "COALESCE( b.customerAddress, '') AS diaChiNguoiNhan, " +
            "b.amount AS tongTien, b.promotionPrice AS tienKhuyenMai, " +
            "COALESCE(c.name, b.customerName, 'Khách lẻ') AS tenKhachHang, " +
            "COALESCE(b.customerName, c.name, '') AS tenNguoiNhan, " +
            "COALESCE(c.phoneNumber, b.customerPhoneNumber, '') AS soDienThoaiKhachHang, " +
            "COALESCE(b.customerPhoneNumber, c.phoneNumber, '') AS soDienThoaiNguoiNhan, " +
            "COALESCE(b.shippingNote, '') AS ghiChuGiaoHang, " +
            "COALESCE(c.email, b.customerEmail, '') AS email, " +
            "b.status AS trangThaiDonHang, " +
            "COALESCE(pm.name, '') AS phuongThucThanhToan, " +
            "COALESCE(pmt.orderId, '') AS maGiaoDich, " +
            "b.invoice_type AS loaiHoaDon, " +
            "COALESCE(d.code, '') AS voucherName, " +
            "b.createDate AS createdDate, " +
            "b.shippingFee AS phiShip " +
            "FROM Bill b " +
            "LEFT JOIN Customer c ON b.customerId = c.id " +
            "LEFT JOIN Discount d ON b.discountId = d.id " +
            "LEFT JOIN PaymentMethod pm ON b.paymentMethodId = pm.id " +
            "LEFT JOIN Payment pmt ON b.id = pmt.billId " +
            "WHERE b.id = :maHoaDon", nativeQuery = true)
    BillDetailDtoInterface getBillDetail(@Param("maHoaDon") Long maHoaDon);

    Bill findTopByOrderByIdDesc();

    @Query(value = "SELECT pd.id, bd.id AS billDetailId, p.id AS productId, p.name AS tenSanPham, " +
            "c.name AS tenMau, s.name AS kichCo, bd.detailPrice AS giaTien, bd.quantity AS soLuong, " +
            "bd.detailPrice  * bd.quantity AS tongTien, " +
            "(SELECT TOP 1 link " +
            " FROM image " +
            " WHERE pd.id = image.productDetailId " + // Liên kết với productDetailId thay vì productId
            ") AS imageUrl " +
            "FROM Bill b " +
            "JOIN BillDetail bd ON b.id = bd.billId " +
            "JOIN ProductDetail pd ON bd.productDetailId = pd.id " +
            "JOIN Product p ON pd.productId = p.id " +
            "JOIN Color c ON pd.colorId = c.id " +
            "JOIN Size s ON pd.sizeId = s.id " +
            "WHERE b.id = :maHoaDon", nativeQuery = true)
    List<BillDetailProduct> getBillDetailProduct(Long maHoaDon);


    @Query(value = "select * from Bill b where DATEDIFF(DAY, b.createDate, GETDATE()) <= 7 and b.status='HOAN_THANH'", nativeQuery = true)
    Page<Bill> findValidBillToReturn(Pageable pageable);

    @Query(value = """
    SELECT pd.id,
           bd.id AS billDetailId,
           p.id AS productId,
           p.name AS tenSanPham,
           c.name AS tenMau,
           s.name AS kichCo,
           bd.detail_price AS giaTien,
           bd.quantity AS soLuong,
           bd.detail_price * bd.quantity AS tongTien,
           (
               SELECT TOP 1 i.link
               FROM image i
               WHERE i.product_id = p.id
           ) AS imageUrl
    FROM Bill b
    JOIN BillDetail bd ON b.id = bd.billId
    JOIN ProductDetail pd ON bd.productDetailId = pd.id
    JOIN Product p ON pd.productId = p.id
    JOIN Color c ON pd.colorId = c.id
    JOIN Size s ON pd.sizeId = s.id
    WHERE b.id = :maHoaDon
    """, nativeQuery = true)
    List<BillDetailProduct> getBillDetailProductBill(@Param("maHoaDon") Long maHoaDon);

}
