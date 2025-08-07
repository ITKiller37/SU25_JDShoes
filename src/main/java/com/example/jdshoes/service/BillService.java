package com.example.jdshoes.service;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface BillService {
    Page<BillDtoInterface> searchListBill(String keyword,
                                          LocalDateTime convertedNgayTaoStart,
                                          LocalDateTime convertedNgayTaoEnd,
                                          String trangThai,
                                          String loaiDon,
                                          Pageable pageable);

    Page<BillDtoInterface> findAll(Pageable pageable);

    String getHtmlContent(Long maHoaDon);

    String exportPdf(HttpServletResponse response, Long maHoaDon) throws DocumentException, IOException;

    Bill updateStatus(String trangThaiDonHang, Long billId);

    BillDetailDtoInterface getBillDetail(Long maHoaDon);

    List<BillDetailProduct> getBillDetailProduct(Long maHoaDon);

    // return
    Page<BillDto> searchBillJson(SearchBillDto searchBillDto, Pageable pageable);

    Page<BillDto> getAllValidBillToReturn( Pageable pageable);

    List<BillDetailProduct> getBillDetailProductBill(Long maHoaDon);

    Map<String, Object> getOrderStatus(String orderCode);

    Page<Bill> getBillByAccount(Pageable pageable);

    Page<Bill> getBillByStatus(String status, Pageable pageable);

    void saveBillHistory(Long billId, BillStatus billStatus, String note, Account account);

    List<BillHistoryDto> getBillHistory(Long maHoaDon);

    Bill findById(Long billId);

    void save(Bill bill);

    void addProductToBill(Long billId, Long productDetailId, Integer quantity);

    BigDecimal getOldShippingFee(Long maHoaDon);

    BigDecimal getOriginalProductAmount(Long maHoaDon);

    void exportToExcel(HttpServletResponse response, Page<BillDtoInterface> bills, String exportUrl) throws IOException;
}



