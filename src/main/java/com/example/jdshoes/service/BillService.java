package com.example.jdshoes.service;

import com.example.jdshoes.dto.Bill.*;
import com.example.jdshoes.entity.Bill;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface BillService {
    Page<BillDtoInterface> searchListBill(String maDinhDanh,
                                          LocalDateTime convertedNgayTaoStart,
                                          LocalDateTime convertedNgayTaoEnd,
                                          String trangThai,
                                          String loaiDon,
                                          String soDienThoai,
                                          String hoVaTen,
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

}



