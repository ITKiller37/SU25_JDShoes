package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderStatusController {


    private final CartService cartService;

    public OrderStatusController( CartService cartService) {

        this.cartService = cartService;
    }

    @ResponseBody
    @GetMapping("/api/getCart")
    public ResponseEntity<CartDto> getCart() {
        try {
            CartDto cartDto = cartService.getCartByAccountId();
            return ResponseEntity.ok(cartDto);
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus(), e.getMessage());
        }
    }
}
