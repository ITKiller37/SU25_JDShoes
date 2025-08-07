package com.example.jdshoes.controller.user;

import com.example.jdshoes.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class CartStatus {

    @Autowired
    private BillService billService;

    @GetMapping("cart-status")
    public String getCartStatus(Model model) {
        return "user/cart-status";
    }

    @ResponseBody
    @GetMapping("/api/order/status")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@RequestParam String orderCode) {
        Map<String, Object> response = billService.getOrderStatus(orderCode);
        if (response.get("order") != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
