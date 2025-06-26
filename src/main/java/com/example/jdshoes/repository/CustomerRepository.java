package com.example.jdshoes.repository;



import com.example.jdshoes.dto.Statistic.TopCustomerBuy;
import com.example.jdshoes.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCode(String code);



    Customer findTopByOrderByIdDesc();

    @Query("SELECT c FROM Customer c WHERE c.code LIKE %:keyword% OR c.name LIKE %:keyword% OR c.phoneNumber LIKE %:keyword%")
    Page<Customer> searchCustomerKeyword(String keyword,Pageable pageable);

    @Query(value = "SELECT LIMIT 5 c.code, c.name, COUNT(c.id) AS totalPurchases, sum(b.amount) as revenue\n" +
            "           FROM Customer c\n" +
            "           JOIN bill b on b.customer_id = c.id\n" +
            "           JOIN bill_detail bd on b.id = bd.bill_id\n" +
            "           GROUP BY c.id, c.name, c.code \n" +
            "           ORDER BY totalPurchases DESC", nativeQuery = true)
    List<TopCustomerBuy> findTopCustomersByPurchases();

    boolean existsByPhoneNumber(String phoneNumber);

    Customer findByPhoneNumber(String phoneNumber);

    Customer findByAccount_Id(Long id);


    @Query("SELECT c FROM Customer c " +
            "JOIN Account a ON a.customer = c " +
            "JOIN Role r ON a.role = r " +
            "WHERE r.name = 'ROLE_USER'")
    Customer findByAll(String fullName, String phone, String email );

    @Query("SELECT c FROM Customer c " +
            "JOIN Account a ON a.customer = c " +
            "JOIN Role r ON a.role = r " +
            "WHERE r.name = 'ROLE_USER'")
    Page<Customer> findAllCustomerRoleUser(Pageable pageable);

    @Query("SELECT c FROM Customer c " +
            "JOIN Account a ON a.customer = c " +
            "JOIN Role r ON a.role = r " +
            "WHERE r.name = 'ROLE_USER' AND " +
            "(c.phoneNumber LIKE :search OR c.name LIKE :search OR c.code LIKE :search)")
    Page<Customer> searchByParamAndRole(@Param("search") String search, Pageable pageable);

}
