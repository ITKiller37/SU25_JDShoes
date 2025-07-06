package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {

    ProductDiscount findByProductDetail_Id(Long productDetailId);

    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.productDetail.id = :productDetailId " +
            "AND CURRENT_TIMESTAMP BETWEEN pd.startDate AND pd.endDate " +
            "AND pd.closed = false")
    ProductDiscount findValidDiscountByProductDetailId(@Param("productDetailId") Long productDetailId);
}
