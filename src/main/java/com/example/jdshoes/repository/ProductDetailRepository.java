package com.example.jdshoes.repository;

import com.example.jdshoes.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    List<ProductDetail> findByProductId(Long productId);

    List<ProductDetail> getProductDetailByProductId(Long productId);

    @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.status = 1 AND pd.quantity > 0")
    List<ProductDetail> findAllActiveWithQuantity();
}
