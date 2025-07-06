package com.example.jdshoes.dto.Bill;

import java.math.BigDecimal;

public interface BillDetailProduct {

    Long getId();
    Long getProductId();

    String getBillDetailId();

    String getImageUrl();
    String getTenSanPham();

    String getTenMau();

    String getKichCo();

    BigDecimal getGiaTien();

    int getSoLuong();

    BigDecimal getTongTien();
}
