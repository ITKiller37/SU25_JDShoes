package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Cart;
import com.example.jdshoes.entity.CartDetail;
import com.example.jdshoes.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    List<CartDetail> findByCartId(Long cartId);

    @Query("SELECT cd FROM CartDetail cd WHERE cd.cart = :cart AND cd.productDetail = :productDetail")
    Optional<CartDetail> findByCartAndProductDetail(@Param("cart") Cart cart, @Param("productDetail") ProductDetail productDetail);
    void deleteAllByCartId(Long cartId);
    List<CartDetail> findAllByCart_Id(Long cartId);

    List<CartDetail> findAllByCartId(Long id);

    @Modifying
    @Query("DELETE FROM CartDetail cd WHERE cd.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}
