package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Brand.BrandDto;
import com.example.jdshoes.entity.Brand;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.BrandRepository;
import com.example.jdshoes.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public BrandDto createBrandApi(BrandDto brandDto) {

        // 1. Kiểm tra nếu tên nhãn hàng rỗng hoặc chỉ chứa khoảng trắng
        if (brandDto.getName() == null || brandDto.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên nhãn hàng");
        }
        // 2. Kiểm tra nếu tên nhãn hàng đã tồn tại
        if (brandRepository.existsByName(brandDto.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên nhãn hàng '" + brandDto.getName() + "' đã tồn tại");
        }

        // 3. Kiểm tra nếu mã nhãn hàng rỗng hoặc chỉ chứa khoảng trắng
        if (brandDto.getCode() == null || brandDto.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã nhãn hàng");
        }
        // 4. Kiểm tra nếu mã nhãn hàng đã tồn tại
        if (brandRepository.existsByCode(brandDto.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã nhãn hàng '" + brandDto.getCode() + "' đã tồn tại");
        }

        Brand brand = convertToEntity(brandDto);
        brand.setStatus(1);
        brand.setDeleteFlag(false);
        Brand brandSaved = brandRepository.save(brand);
        return convertToDto(brandSaved);
    }

    @Override
    public Page<Brand> getAllBrand(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    @Override
    public Brand createBrand(Brand brand) {
        // Kiểm tra mã
        if (brand.getCode() == null || brand.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã nhãn hàng");
        }
        if (brandRepository.existsByCode(brand.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã nhãn hàng '" + brand.getCode() + "' đã tồn tại");
        }

        // Kiểm tra tên
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên nhãn hàng");
        }
        if (brandRepository.existsByName(brand.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên nhãn hàng '" + brand.getName() + "' đã tồn tại");
        }
        brand.setStatus(1);
        brand.setDeleteFlag(false);
        return brandRepository.save(brand);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return brandRepository.existsById(id);
    }

    @Override
    public Brand updateBrand(Long id, Brand brand) {
        // Lấy brand hiện có từ DB
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ShoesApiException(HttpStatus.NOT_FOUND, "Không tìm thấy nhãn hàng với ID: " + id));

        // Kiểm tra mã nhãn hàng
        if (brand.getCode() == null || brand.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã nhãn hàng");
        }
        // Chỉ kiểm tra trùng mã nếu mã mới khác mã cũ
        if (!existingBrand.getCode().equals(brand.getCode().trim())) {
            if (brandRepository.existsByCode(brand.getCode().trim())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã nhãn hàng '" + brand.getCode() + "' đã tồn tại");
            }
        }

        // Kiểm tra tên khi sửa
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên nhãn hàng");
        }
        // Chỉ kiểm tra trùng tên nếu tên mới khác tên cũ, và tên đó không phải của chính bản ghi đang sửa
        if (!existingBrand.getName().equals(brand.getName().trim())) {
            if (brandRepository.existsByNameAndIdNot(brand.getName().trim(), id)) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên nhãn hàng '" + brand.getName() + "' đã tồn tại");
            }
        }

        // Cập nhật các trường của existingBrand
        existingBrand.setCode(brand.getCode().trim());
        existingBrand.setName(brand.getName().trim());
        existingBrand.setStatus(brand.getStatus()); // Giữ nguyên trạng thái hoặc cập nhật nếu có từ request
        existingBrand.setDeleteFlag(false); // Giữ nguyên deleteFlag hoặc cập nhật nếu có từ request

        return brandRepository.save(existingBrand);
    }

    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(null);
        brand.setDeleteFlag(true);
        brandRepository.save(brand);
    }

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
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
