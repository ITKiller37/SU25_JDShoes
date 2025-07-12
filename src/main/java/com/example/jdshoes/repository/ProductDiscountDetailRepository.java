package com.example.jdshoes.repository;

import com.example.jdshoes.entity.ProductDiscountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDiscountDetailRepository extends JpaRepository<ProductDiscountDetail, Long> {

    List<ProductDiscountDetail> findAllByProductDiscountId(Long discountId);
}
