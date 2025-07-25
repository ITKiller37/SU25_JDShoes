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
            "           JOIN billDetail bd on b.id = bd.bill_id\n" +
            "           GROUP BY c.id, c.name, c.code \n" +
            "           ORDER BY totalPurchases DESC", nativeQuery = true)
    List<TopCustomerBuy> findTopCustomersByPurchases();

    boolean existsByPhoneNumber(String phoneNumber);

    Customer findByPhoneNumber(String phoneNumber);

    Customer findByAccount_Id(Long id);

    Customer findByAccount_Email(String email);

    @Query("select c from Customer c where c.name = ?1 and c.phoneNumber = ?2 and c.email = ?3 and (c.deleted is null or c.deleted = false)")
    Customer findByAll(String fullName, String phone, String email );

    @Query("select c from Customer c where (c.phoneNumber like ?1 or c.name like ?1 or c.code like ?1) and (c.deleted is null or c.deleted = false)")
    Page<Customer> findByParam(String search, Pageable pageable);

    @Query("select c from Customer c where (c.deleted is null or c.deleted = false)")
    Page<Customer> findAll(Pageable pageable);
    @Query("SELECT c FROM Customer c WHERE c.account.role.name = com.example.jdshoes.entity.enumClass.RoleName.ROLE_USER AND c.deleted = false")
    Page<Customer> findAllUserCustomers(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.account.role.name = com.example.jdshoes.entity.enumClass.RoleName.ROLE_USER AND c.deleted = false AND (c.name LIKE :search OR c.email LIKE :search OR c.phoneNumber LIKE :search)")
    Page<Customer> findAllUserCustomersBySearch(@Param("search") String search, Pageable pageable);

}
