package com.example.jdshoes.repository;

import com.example.jdshoes.entity.OrderTemp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTempRepository extends JpaRepository<OrderTemp, String> {
    OrderTemp findByOrderId(String orderId);

    OrderTemp findByVnpTxnRef(String vnpTxnRef);
}
