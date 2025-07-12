package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Category> findAllByDeleteFlagFalse();
}
