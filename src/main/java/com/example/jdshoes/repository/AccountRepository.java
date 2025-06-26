package com.example.jdshoes.repository;


import com.example.jdshoes.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>{

    Account findByEmail(String email);


    @Query("select a from Account a where a.email = ?1 and a.id <> ?2")
    Account findByEmail(String email, Long id);


    @Query(value = "SELECT CONCAT('T', MONTH(a.create_date)) AS month, COUNT(a.id) AS count FROM Account a" +
            " WHERE a.create_date between '2023-01-01' AND '2023-12-31' " +
            "GROUP BY MONTH(create_date)", nativeQuery = true)
    List<Object[]> getMonthlyAccountStatistics(String startDate, String endDate);

    Account findByCustomer_PhoneNumber(String phoneNumber);

    Account findTopByOrderByIdDesc();
}
