package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.enumClass.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByEmail(String email);

    @Query("select a from Account a where a.email = ?1 and a.id <> ?2")
    Account findByEmail(String email, Long id);

    @Query("select a from Account a where a.email = ?1")
    Optional<Account> findByEmailOpt(String email);

    @Query(value = "SELECT CONCAT('T', MONTH(a.createDate)) AS month, COUNT(a.id) AS count FROM Account a" +
            " WHERE a.createDate between '2023-01-01' AND '2023-12-31' " +
            "GROUP BY MONTH(createDate)", nativeQuery = true)
    List<Object[]> getMonthlyAccountStatistics(String startDate, String endDate);

    Account findByCustomer_PhoneNumber(String phoneNumber);

    Account findTopByOrderByIdDesc();

    Page<Account> findByRole_Name(RoleName role, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.role.name = ?1 AND " +
            "(a.email LIKE %?2% OR a.customer.name LIKE %?2%)")
    Page<Account> searchEmployeeByEmailOrName(RoleName role, String keyword, Pageable pageable);

    @Query("select a from Account a where a.customer.id = ?1")
    Account findByCustomer(Long id);
}
