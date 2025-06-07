package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Brand.BrandDto;
import com.example.jdshoes.entity.Brand;
import com.example.jdshoes.repository.BrandRepository;
import com.example.jdshoes.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public BrandDto createBrandApi(BrandDto brandDto) {

        Brand brand = convertToEntity(brandDto);
        brand.setStatus(1);
        brand.setDeleteFlag(false);
        Brand brandSaved = brandRepository.save(brand);
        return convertToDto(brandSaved);
    }

    private Brand convertToEntity(BrandDto brandDto) {
        Brand brand = new Brand();
        brand.setCode(brandDto.getCode());
        brand.setName(brandDto.getName());
        brand.setId(brandDto.getId());
        return brand;
    }

    private BrandDto convertToDto(Brand brand) {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(brand.getId());
        brandDto.setCode(brand.getCode());
        brandDto.setName(brand.getName());
        return brandDto;
    }
}
