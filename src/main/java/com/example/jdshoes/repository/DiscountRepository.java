package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    @Query(
            """
                    SELECT d FROM Discount d WHERE
                    (:searchTerm IS NULL OR
                    LOWER(d.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.startDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.endDate AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.discountAmount AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.percentage AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.maximumAmount AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                    CAST(d.minimumAmount AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    )
            """
    )
    Page<Discount> findBySingleSearchTermPaged(
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}