package com.example.jdshoes.repository;


import com.example.jdshoes.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    List<Image> findAllByProductDetailId(Long productDetailId);
}
