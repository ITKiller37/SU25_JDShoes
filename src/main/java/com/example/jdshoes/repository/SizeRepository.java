package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SizeRepository extends JpaRepository<Size, Long> {

    @Query(value = "select distinct s from Size s join ProductDetail pd on s.id = pd.size.id where pd.product = :product")
    List<Size> findSizesByProduct(Product product);

    @Query(value = "select distinct s from Size s join ProductDetail pd on s.id = pd.size.id where pd.product = :product and pd.color = :color")
    List<Size> findSizesByProductAndColor(Product product, Color color);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Size> findAllByDeleteFlagFalse();
}