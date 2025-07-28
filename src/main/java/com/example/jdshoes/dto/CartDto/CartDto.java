package com.example.jdshoes.dto.CartDto;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Long id;
    private Long customerId;
    private List<CartItem> items; // Danh sách các mục chi tiết trong giỏ hàng
    private LocalDateTime createDate;
    private LocalDateTime updatedDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem {
        private Long id; // ID của CartDetail
        private int quantity;
        private LocalDateTime createDate;
        private LocalDateTime updatedDate;
        private ProductCart product; // Thông tin sản phẩm
        private ProductDetailDto productDetail; // Thông tin chi tiết sản phẩm
    }
}
