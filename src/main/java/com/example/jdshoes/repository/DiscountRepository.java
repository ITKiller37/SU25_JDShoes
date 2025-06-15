package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    @Query(
            """
                    SELECT d FROM Discount d WHERE
                    (:searchTerm IS NULL OR LOWER(d.code) LIKE %:searchTerm% OR LOWER(d.name) LIKE %:searchTerm% OR LOWER(d.type) LIKE %:searchTerm%) AND
                    (:status IS NULL OR d.status = :status) AND
                    (:filterStartDate IS NULL OR d.startDate >= :filterStartDate) AND
                    (:filterEndDate IS NULL OR d.endDate <= :filterEndDate)
            """
            )
    List<Discount> findBySearchParams(
            @Param("searchTerm") String searchTerm,
            @Param("status") Boolean status,
            @Param("filterStartDate") LocalDate filterStartDate,
            @Param("filterEndDate") LocalDate filterEndDate);

}
