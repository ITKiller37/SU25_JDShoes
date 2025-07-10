package com.example.jdshoes.service.Impl;

import com.example.jdshoes.dto.Category.CategoryDto;
import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.ProductRepository;
import com.example.jdshoes.repository.SizeRepository;
import com.example.jdshoes.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Color> getColorByProductId(Long productId) throws NotFoundException {

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return colorRepository.findColorsByProduct(product);
    }

    @Override
    public List<Color> getColorByProductIdAndSizeId(Long productId, Long sizeId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new NotFoundException("Size not found"));
        return colorRepository.findColorsByProductAndSize(product, size);
    }

    @Override
    public ColorDto createColorApi(ColorDto colorDto) {

        // Kiểm tra tên và mã khi tạo Color qua API
        if (colorDto.getName() == null || colorDto.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên màu");
        }
        if (colorRepository.existsByName(colorDto.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên màu '" + colorDto.getName() + "' đã tồn tại");
        }

        if (colorDto.getCode() == null || colorDto.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã màu");
        }
        if (colorRepository.existsByCode(colorDto.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã màu '" + colorDto.getCode() + "' đã tồn tại");
        }


        Color color = convertToEntity(colorDto);
        color.setDeleteFlag(false);
        Color savedColor = colorRepository.save(color);
        return convertToDto(savedColor);
    }

    @Override
    public Page<Color> findAll(Pageable pageable) {
        return colorRepository.findAll(pageable);
    }

    @Override
    public Color createColor(Color color) {
        // Kiểm tra mã
        if (color.getCode() == null || color.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã màu");
        }
        if(colorRepository.existsByCode(color.getCode().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã màu '" + color.getCode() + "' đã tồn tại");
        }

        // THÊM KIỂM TRA TÊN KHI THÊM
        if (color.getName() == null || color.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên màu");
        }
        if (colorRepository.existsByName(color.getName().trim())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên màu '" + color.getName() + "' đã tồn tại");
        }

        color.setDeleteFlag(false);
        return colorRepository.save(color);
    }

    @Override
    public boolean existsById(Long id) {
        return colorRepository.existsById(id);
    }

    @Override
    public Color updateColor(Color color) {
        // Lấy color hiện có từ DB
        Color existingColor = colorRepository.findById(color.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy màu với ID: " + color.getId()));

        // Kiểm tra mã màu
        if (color.getCode() == null || color.getCode().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mã màu");
        }
        // Chỉ kiểm tra trùng mã nếu mã mới khác mã cũ
        if(!existingColor.getCode().equals(color.getCode().trim())) {
            if(colorRepository.existsByCode(color.getCode().trim())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã màu '" + color.getCode() + "' đã tồn tại");
            }
        }

        // THÊM KIỂM TRA TÊN KHI SỬA
        if (color.getName() == null || color.getName().trim().isEmpty()) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên màu");
        }
        // Chỉ kiểm tra trùng tên nếu tên mới khác tên cũ, và tên đó không phải của chính bản ghi đang sửa
        if (!existingColor.getName().equals(color.getName().trim())) {
            if (colorRepository.existsByNameAndIdNot(color.getName().trim(), color.getId())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tên màu '" + color.getName() + "' đã tồn tại");
            }
        }

        // Cập nhật các trường của existingColor
        existingColor.setCode(color.getCode().trim());
        existingColor.setName(color.getName().trim());


        return colorRepository.save(existingColor);
    }

    @Override
    public Optional<Color> findById(Long id) {
        return colorRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Color existingColor = colorRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy màu có id " + id));
        existingColor.setDeleteFlag(!existingColor.getDeleteFlag()); // Đảo ngược trạng thái
        colorRepository.save(existingColor);
    }

    @Override
    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    @Override
    public List<Color> getAllActive() {
        return colorRepository.findAllByDeleteFlagFalse();
    }

    private Color convertToEntity(ColorDto colorDto) {
        Color color = new Color();
        color.setCode(colorDto.getCode());
        color.setName(colorDto.getName());
        color.setId(colorDto.getId());
        return color;
    }

    private ColorDto convertToDto(Color color) {
        ColorDto colorDto = new ColorDto();
        colorDto.setId(color.getId());
        colorDto.setCode(color.getCode());
        colorDto.setName(color.getName());
        return colorDto;
    }
}
