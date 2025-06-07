package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Material;
import com.example.jdshoes.repository.MaterialRepository;
import com.example.jdshoes.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;


    @Override
    public MaterialDto createMaterialApi(MaterialDto materialDto) {

        Material material = convertToEntity(materialDto);
        material.setStatus(1);
        material.setDeleteFlag(false);
        Material savedMaterial = materialRepository.save(material);
        return convertToDto(savedMaterial);
    }

    private Material convertToEntity(MaterialDto materialDto) {
        Material material = new Material();
        material.setId(materialDto.getId());
        material.setCode(materialDto.getCode());
        material.setName(materialDto.getName());
        return material;
    }

    private MaterialDto convertToDto(Material material) {
        MaterialDto materialDto = new MaterialDto();
        materialDto.setId(material.getId());
        materialDto.setCode(material.getCode());
        materialDto.setName(material.getName());
        return materialDto;
    }
}
