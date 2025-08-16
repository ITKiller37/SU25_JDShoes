package com.example.jdshoes.repository;

import com.example.jdshoes.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {


    @Query("""
    SELECT pd FROM ProductDiscount pd
    WHERE pd.closed = true
    """)
    List<ProductDiscount> findDeletedDiscounts();


    @Query("""
    SELECT pd FROM ProductDiscount pd
    JOIN pd.productDiscountDetails pdd
    WHERE pdd.productDetail.id = :productDetailId
    AND CURRENT_TIMESTAMP BETWEEN pd.startDate AND pd.endDate
    AND pd.closed = false
""")
    ProductDiscount findValidDiscountByProductDetailId(@Param("productDetailId") Long productDetailId);
}
