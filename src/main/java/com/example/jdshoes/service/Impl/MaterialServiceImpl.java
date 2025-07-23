package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.entity.Material;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
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

        // Kiểm tra tên và mã khi tạo Material qua API
        if (materialDto.getName() == null || materialDto.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên chất liệu");
        }
        if (materialRepository.existsByName(materialDto.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên chất liệu '" + materialDto.getName() + "' đã tồn tại");
        }

        if (materialDto.getCode() == null || materialDto.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã chất liệu");
        }
        if (materialRepository.existsByCode(materialDto.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã chất liệu '" + materialDto.getCode() + "' đã tồn tại");
        }

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
        // Kiểm tra mã
        if (material.getCode() == null || material.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã chất liệu");
        }
        if(materialRepository.existsByCode(material.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã chất liệu '" + material.getCode() + "' đã tồn tại");
        }

        // THÊM KIỂM TRA TÊN KHI THÊM
        if (material.getName() == null || material.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên chất liệu");
        }
        if (materialRepository.existsByName(material.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên chất liệu '" + material.getName() + "' đã tồn tại");
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
        Material existingMaterial = materialRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy chât liệu có id " + id));
        existingMaterial.setDeleteFlag(!existingMaterial.getDeleteFlag()); // Đảo ngược trạng thái
        materialRepository.save(existingMaterial);
    }

    @Override
    public Material updateMaterial(Material material) {
        // Lấy material hiện có từ DB
        Material existingMaterial = materialRepository.findById(material.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chất liệu với ID: " + material.getId())); // Thêm thông báo lỗi rõ ràng

        // Kiểm tra mã chất liệu
        if (material.getCode() == null || material.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã chất liệu");
        }
        // Chỉ kiểm tra trùng mã nếu mã mới khác mã cũ
        if(!existingMaterial.getCode().equals(material.getCode().trim())) {
            if(materialRepository.existsByCode(material.getCode().trim())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã chất liệu '" + material.getCode() + "' đã tồn tại");
            }
        }

        // THÊM KIỂM TRA TÊN KHI SỬA
        if (material.getName() == null || material.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên chất liệu");
        }
        // Chỉ kiểm tra trùng tên nếu tên mới khác tên cũ, và tên đó không phải của chính bản ghi đang sửa
        if (!existingMaterial.getName().equals(material.getName().trim())) {
            if (materialRepository.existsByNameAndIdNot(material.getName().trim(), material.getId())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên chất liệu '" + material.getName() + "' đã tồn tại");
            }
        }

        // Cập nhật các trường của existingMaterial
        existingMaterial.setCode(material.getCode().trim());
        existingMaterial.setName(material.getName().trim());
        existingMaterial.setStatus(material.getStatus()); // Giữ nguyên trạng thái hoặc cập nhật nếu có từ request

        return materialRepository.save(existingMaterial);
    }

    @Override
    public List<Material> getAll() {
        return materialRepository.findAll();
    }

    @Override
    public List<Material> getAllActive() {
        return materialRepository.findAllByDeleteFlagFalse();
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