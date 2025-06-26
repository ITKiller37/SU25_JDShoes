package com.example.jdshoes.service.serviceImpl;



import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.entity.Material;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShopApiException;
import com.example.jdshoes.repository.MaterialRepository;
import com.example.jdshoes.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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

    @Override
    public Page<Material> getAllMaterial(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }

    @Override
    public Material createMaterial(Material material) {
        if(materialRepository.existsByCode(material.getCode())) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã chất liệu " + material.getCode() + " đã tồn tại");
        }
        material.setDeleteFlag(false);
        return materialRepository.save(material);
    }

    @Override
    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Material material = materialRepository.findById(id).orElseThrow(null);
        material.setDeleteFlag(true);
        materialRepository.save(material);
    }

    @Override
    public Material updateMaterial(Material material) {
        Material existingMaterial = materialRepository.findById(material.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy chất liệu"));
        if(!existingMaterial.getCode().equals(material.getCode())) {
            if(materialRepository.existsByCode(material.getCode())) {
                throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã chất liệu " + material.getCode() + " đã tồn tại");
            }
        }
        material.setDeleteFlag(false);
        return materialRepository.save(material);
    }

    @Override
    public List<Material> getAll() {
        return materialRepository.findAll();
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
