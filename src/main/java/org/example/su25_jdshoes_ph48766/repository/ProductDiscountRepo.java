package org.example.su25_jdshoes_ph48766.repository;

import org.example.su25_jdshoes_ph48766.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDiscountRepo extends JpaRepository<ProductDiscount, Long> {

}
