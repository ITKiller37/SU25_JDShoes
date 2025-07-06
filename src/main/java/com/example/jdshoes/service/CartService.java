package com.example.jdshoes.service;

import com.example.jdshoes.dto.Cart.CartItemDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartItemDto> getCartByCustomerId(Long customerId);
    void addToCart(CartItemDto cartItemDto) throws NotFoundException;
    void updateCartItem(CartItemDto cartItemDto) throws NotFoundException;
    void removeFromCart(Long cartDetailId) throws NotFoundException;
    void clearCart(Long customerId) throws NotFoundException;
    void orderUser(OrderDto orderDto);
    OrderDto orderAtCounter(OrderDto orderDto);
}
