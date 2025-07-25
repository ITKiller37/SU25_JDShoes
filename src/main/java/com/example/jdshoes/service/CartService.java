package com.example.jdshoes.service;

import com.example.jdshoes.dto.Cart.CartItemDto;
import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CartService {

    CartDto addToCart(CartItemDto cartItemDto) throws NotFoundException;
    CartDto updateCartItem(CartItemDto cartItemDto) throws NotFoundException;
    CartDto removeFromCart(Long cartDetailId) throws NotFoundException;
    CartDto clearCart() throws NotFoundException;
    Map<String, Object> orderUser(OrderDto orderDto);
    OrderDto orderAtCounter(OrderDto orderDto);
    CartDto getCartByAccountId();

    void completeOrderAfterPayment(OrderDto orderDto, String orderId);
}
