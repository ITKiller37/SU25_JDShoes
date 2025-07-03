package com.example.jdshoes.service;

import com.example.jdshoes.dto.CartDto.CartDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartDto> getAllCartByAccountId();
}
