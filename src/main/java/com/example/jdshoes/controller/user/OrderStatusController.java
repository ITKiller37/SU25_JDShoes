package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
@Controller
public class OrderStatusController {


    private final CartService cartService;

    public OrderStatusController( CartService cartService) {

        this.cartService = cartService;
    }

    @ResponseBody
    @GetMapping("/api/getAllCart")
    public List<CartDto> getAllCart() {
        return cartService.getAllCartByAccountId();
    }
}
