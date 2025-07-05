package com.example.jdshoes.service;

import com.example.jdshoes.dto.Brand.BrandDto;
import com.example.jdshoes.entity.Brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BrandService {


    BrandDto createBrandApi( BrandDto brandDto);

    Page<Brand> getAllBrand(Pageable pageable);

    Brand createBrand(Brand brand);

    Optional<Brand> findById(Long id);

    boolean existsById(Long id);

    Brand updateBrand(Long id, Brand brand);

    void delete(Long id);

    List<Brand> getAll();
}
