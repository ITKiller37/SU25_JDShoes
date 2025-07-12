package com.example.jdshoes.repository;



import com.example.jdshoes.entity.AddressShipping;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface AddressShippingRepository extends JpaRepository<AddressShipping, Long> {
    List<AddressShipping> findAllByCustomer_Account_Id(Long accountId);

    @Modifying
    @Query("UPDATE AddressShipping a SET a.isDefault = false WHERE a.customer.id = :customerId")
    void resetDefaultAddress(@Param("customerId") Long customerId);

    @Modifying
    @Query("UPDATE AddressShipping a SET a.isDefault = false WHERE a.customer.id = ?1")
    void updateAllIsDefaultFalseByCustomer(Long customerId);

}
