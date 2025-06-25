package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {

    ProductDiscount findByProductDetail_Id(Long productDetailId);

//    @Query(value = "SELECT * FROM ProductDiscount pd " +
//            "WHERE pd.productDetail = :productDetailId " +
//            "AND GETDATE() BETWEEN pd.startDate AND pd.endDate " +
//            "AND pd.closed = 'false'", nativeQuery = true)
//    ProductDiscount findValidDiscountByProductDetailId(@Param("productDetailId") Long productDetailId);
}
