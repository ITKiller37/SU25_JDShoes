package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerId(Long customerId);
    boolean existsByCustomerId(Long customerId);
    List<Cart> findAllByCustomer_Id(Long customerId);
    Optional<Cart> findBySessionId(String sessionId);

}
