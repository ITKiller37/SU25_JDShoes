
package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.repository.SizeRepository;
import com.example.jdshoes.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public SizeDto createSizeApi(SizeDto sizeDto) {

        // Kiểm tra tên và mã khi tạo Size qua API
        if (sizeDto.getName() == null || sizeDto.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên cỡ");
        }
        if (sizeRepository.existsByName(sizeDto.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên cỡ '" + sizeDto.getName() + "' đã tồn tại");
        }

        if (sizeDto.getCode() == null || sizeDto.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã cỡ");
        }
        if (sizeRepository.existsByCode(sizeDto.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã cỡ '" + sizeDto.getCode() + "' đã tồn tại");
        }


        Size size = convertToEntity(sizeDto);
        size.setDeleteFlag(false);
        Size savedSize = sizeRepository.save(size);
        return convertToDto(savedSize);
    }

    @Override
    public List<Size> getSizesByProductId(Long productId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return sizeRepository.findSizesByProduct(product);
    }

    @Override
    public List<Size> getSizesByProductIdAndColorId(Long productId, Long colorId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Color color = colorRepository.findById(colorId).orElseThrow(() -> new NotFoundException("Color not found"));;

        return sizeRepository.findSizesByProductAndColor(product, color);
    }

    @Override
    public Page<Size> getAllSize(Pageable pageable) {
        return sizeRepository.findAll(pageable);
    }

    @Override
    public Size createSize(Size size) {
        // Kiểm tra mã
        if (size.getCode() == null || size.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã cỡ");
        }
        if(sizeRepository.existsByCode(size.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã cỡ '" + size.getCode() + "' đã tồn tại");
        }

        // THÊM KIỂM TRA TÊN KHI THÊM
        if (size.getName() == null || size.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên cỡ");
        }
        if (sizeRepository.existsByName(size.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên cỡ '" + size.getName() + "' đã tồn tại");
        }

        size.setDeleteFlag(false);
        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(Size size) {
        // Lấy size hiện có từ DB
        Size existingSize = sizeRepository.findById(size.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cỡ với ID: " + size.getId())); // Thêm thông báo lỗi rõ ràng

        // Kiểm tra mã cỡ
        if (size.getCode() == null || size.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã cỡ");
        }
        // Chỉ kiểm tra trùng mã nếu mã mới khác mã cũ
        if(!existingSize.getCode().equals(size.getCode().trim())) {
            if(sizeRepository.existsByCode(size.getCode().trim())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã cỡ '" + size.getCode() + "' đã tồn tại");
            }
        }

        // THÊM KIỂM TRA TÊN KHI SỬA
        if (size.getName() == null || size.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên cỡ");
        }
        // Chỉ kiểm tra trùng tên nếu tên mới khác tên cũ, và tên đó không phải của chính bản ghi đang sửa
        if (!existingSize.getName().equals(size.getName().trim())) {
            if (sizeRepository.existsByNameAndIdNot(size.getName().trim(), size.getId())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên cỡ '" + size.getName() + "' đã tồn tại");
            }
        }

        // Cập nhật các trường của existingSize
        existingSize.setCode(size.getCode().trim());
        existingSize.setName(size.getName().trim());
        existingSize.setDeleteFlag(false); // Giữ nguyên deleteFlag hoặc cập nhật nếu có từ request

        return sizeRepository.save(size);
    }

    @Override
    public Optional<Size> findById(Long id) {
        return sizeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Size size = sizeRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy cỡ có id " + id) );
        size.setDeleteFlag(true);
        sizeRepository.save(size);
    }

    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    private Size convertToEntity(SizeDto sizeDto) {
        Size size = new Size();
        size.setId(sizeDto.getId());
        size.setName(sizeDto.getName());
        size.setCode(sizeDto.getCode());
        return size;
    }

    private SizeDto convertToDto(Size size) {
        SizeDto sizeDto = new SizeDto();
        sizeDto.setId(size.getId());
        sizeDto.setName(size.getName());
        sizeDto.setCode(size.getCode());
        return sizeDto;
    }
    @Override
    public List<Size> getSizesByProductIdH(Long productId) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductId(productId);
        return productDetails.stream()
                .map(ProductDetail::getSize)
                .distinct()
                .collect(Collectors.toList());
    }
}

