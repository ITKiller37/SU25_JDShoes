package com.example.jdshoes.repository;



import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    @Query(value = "select distinct c from Color c join ProductDetail pd on c.id = pd.color.id where pd.product = :product")
    List<Color> findColorsByProduct(Product product);

    @Query(value = "select distinct c from Color c join ProductDetail pd on c.id = pd.color.id where pd.product = :product and pd.size = :size")
    List<Color> findColorsByProductAndSize(Product product, Size size);

    boolean existsByCode(String code);
}
