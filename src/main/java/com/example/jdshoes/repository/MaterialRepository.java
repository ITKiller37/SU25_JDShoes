package com.example.jdshoes.repository;

import com.example.jdshoes.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Material> findAllByDeleteFlagFalse();
}
