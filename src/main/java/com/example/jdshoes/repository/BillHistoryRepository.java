package com.example.jdshoes.repository;

import com.example.jdshoes.entity.BillHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillHistoryRepository extends JpaRepository<BillHistory, Long> {
    List<BillHistory> findByBillIdOrderByCreatedAtAsc(Long maHoaDon);
}
