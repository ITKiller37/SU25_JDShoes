package com.example.jdshoes.service;

import com.example.jdshoes.dto.Brand.BrandDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface BrandService {


    BrandDto createBrandApi( BrandDto brandDto);
}
