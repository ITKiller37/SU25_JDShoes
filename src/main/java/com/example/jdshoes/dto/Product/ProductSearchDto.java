package com.example.jdshoes.dto.Product;

public interface ProductSearchDto {

    Long getIdSanPham();
    String getMaSanPham();
    String getTenSanPham();
    String getNhanHang();
    String getChatLieu();
    String getTheLoai();

    String getTrangThai();
    Integer getTotalQuantity();
}
