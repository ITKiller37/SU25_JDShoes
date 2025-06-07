package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByDeleteFlagFalse(Pageable pageable);

    @Query("select p from Product p join ProductDetail pd on p.id = pd.product.id where pd.id = :productDetailId")
    Product findByProductDetail_Id(Long productDetailId);
}
