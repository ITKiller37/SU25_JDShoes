package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    List<ProductDetail> findByProductIdAndSizeAndColor(Long productId, Size size, Color color);
}
