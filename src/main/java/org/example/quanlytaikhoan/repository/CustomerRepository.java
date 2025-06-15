package org.example.quanlytaikhoan.repository;


import org.example.quanlytaikhoan.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCode(String code);


    Customer findTopByOrderByIdDesc();

    @Query("SELECT c FROM Customer c WHERE c.code LIKE %:keyword% OR c.name LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    Page<Customer> searchCustomerKeyword(String keyword,Pageable pageable);

}
