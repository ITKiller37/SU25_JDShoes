package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.CheckOrderDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderRestController {

    private final CartService cartService;

    @PostMapping("/orderUser")
    public ResponseEntity<String> createUserOrder(@RequestBody OrderDto orderDto) {
        // Xử lý đơn hàng từ phía người dùng
        cartService.orderUser(orderDto);
        return ResponseEntity.ok("Đơn hàng đã được tạo thành công");
    }

    @PostMapping("/orderAdmin")
    public ResponseEntity<OrderDto> createAdminOrder(@RequestBody OrderDto orderDto) {
        // Xử lý đơn hàng từ phía admin
        OrderDto createdOrder = cartService.orderAtCounter(orderDto);
        return ResponseEntity.ok(createdOrder);
    }
//
//    @PostMapping("/check")
//    public ResponseEntity<List<CheckOrderDto>> checkOrderAvailability(@RequestBody List<CheckOrderDto> checkOrderDtoList) {
//        // Kiểm tra tính khả dụng của đơn hàng
//        List<CheckOrderDto> result = orderService.checkOrderAvailability(checkOrderDtoList);
//        return ResponseEntity.ok(result);
//    }

//    @GetMapping("/user/{customerId}")
//    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Long customerId) {
//        // Lấy danh sách đơn hàng của khách hàng
//        List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
//        return ResponseEntity.ok(orders);
//    }
}
