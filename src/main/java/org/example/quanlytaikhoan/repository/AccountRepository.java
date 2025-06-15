package org.example.quanlytaikhoan.repository;


import org.example.quanlytaikhoan.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AccountRepository extends JpaRepository<Account, Long>{

    Account findByEmail(String email);


    @Query(value = "SELECT CONCAT('T', MONTH(a.create_date)) AS month, COUNT(a.id) AS count " +
            "FROM Account a WHERE a.create_date between '2023-01-01' AND '2023-12-31' " +
            "GROUP BY MONTH(create_date)", nativeQuery = true)

    List<Object[]> getMonthlyAccountStats();


    @Query("SELECT a FROM Account a WHERE a.email LIKE %:keyword%")
    Page<Account> searchAccounts(@Param("keyword") String keyword, Pageable pageable);





}
