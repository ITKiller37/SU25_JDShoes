package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    List<Image> findAllByProductDetailId(Long productDetailId);
}
