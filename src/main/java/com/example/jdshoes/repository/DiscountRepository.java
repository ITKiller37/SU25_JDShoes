package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    @Query("SELECT d FROM Discount d " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR d.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR d.endDate <= :endDate) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:maximumUsage IS NULL OR d.maximumUsage = :maximumUsage)")
    Page<Discount> findDiscountsByFilter(
            @Param("keyword") String keyword,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("status") Integer status,
            @Param("maximumUsage") Integer maximumUsage,
            Pageable pageable);


    @Query("SELECT d FROM Discount d WHERE d.deleteFlag = true ORDER BY d.id DESC")
    Page<Discount> findAllByOrderByIdDesc(Pageable pageable);

}