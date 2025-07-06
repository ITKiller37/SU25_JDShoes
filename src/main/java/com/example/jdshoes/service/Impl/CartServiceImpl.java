package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.Cart;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDiscount;
import com.example.jdshoes.service.CartService;
import com.example.jdshoes.utils.UserLoginUtil;

import java.util.ArrayList;
import java.util.List;

public class CartServiceImpl implements CartService {

    @Override
    public List<CartDto> getAllCartByAccountId() {
//        Account account = UserLoginUtil.getCurrentLogin();
//        List<Cart> cartList = cartRepository.findAllByAccount_Id(account.getId());
        List<CartDto> cartDtos = new ArrayList<>();
//        cartList.forEach(cart -> {
//            Product product = productRepository.findById(cart.getProductDetail().getProduct().getId()).orElseThrow();
//            ProductCart productCart = new ProductCart();
//            productCart.setProductId(product.getId());
//            productCart.setName(product.getName());
//            productCart.setCode(product.getCode());
//            productCart.setDescribe(product.getDescribe());
//            productCart.setImageUrl(product.getImage().get(0).getLink());
//            ProductDetailDto productDetailDto = new ProductDetailDto();
//            productDetailDto.setId(cart.getProductDetail().getId());
//            productDetailDto.setProductId(product.getId());
//            productDetailDto.setPrice(cart.getProductDetail().getPrice());
//            productDetailDto.setSize(cart.getProductDetail().getSize());
//            productDetailDto.setQuantity(cart.getProductDetail().getQuantity());
//            productDetailDto.setColor(cart.getProductDetail().getColor());
//
//            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(cart.getProductDetail().getId());
//            if(productDiscount != null) {
//                productDetailDto.setDiscountedPrice(productDiscount.getDiscountedAmount());
//            }
//
//            CartDto cartDto = new CartDto();
//            cartDto.setId(cart.getId());
//            cartDto.setQuantity(cart.getQuantity());
//            cartDto.setCreateDate(cart.getCreateDate());
//            cartDto.setAccountId(Long.parseLong("2"));
//            cartDto.setProduct(productCart);
//            cartDto.setDetail(productDetailDto);
//            cartDtos.add(cartDto);
//        });
        return cartDtos;
    }
}
