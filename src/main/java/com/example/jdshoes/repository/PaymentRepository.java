package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(String txnRef);

    Optional<Payment> findFirstByOrderId(String orderId);

    Payment findByVnpTxnRef(String vnpTxnRef);
}
