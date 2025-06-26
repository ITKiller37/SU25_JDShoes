package com.example.jdshoes.service;


import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface MaterialService {

    MaterialDto createMaterialApi(MaterialDto materialDto);

    Page<Material> getAllMaterial(Pageable pageable);

    Material createMaterial(Material material);

    Optional<Material> findById(Long id);

    void delete(Long id);

    Material updateMaterial(Material material);

    List<Material> getAll();
}
