package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Cart;
import com.example.jdshoes.entity.CartDetail;
import com.example.jdshoes.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    List<CartDetail> findByCartId(Long cartId);
    Optional<CartDetail> findByCartAndProductDetail(Cart cart, ProductDetail productDetail);
    void deleteAllByCartId(Long cartId);
    List<CartDetail> findAllByCart_Id(Long cartId);
}
