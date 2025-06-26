package com.example.jdshoes.service.serviceImpl;



import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShopApiException;
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
        if(colorRepository.existsByCode(color.getCode())) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã màu " + color.getCode() + " đã tồn tại");
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
        Color existingColor = colorRepository.findById(color.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy màu có mã " + color.getCode()) );
        if(!existingColor.getCode().equals(color.getCode())) {
            if(colorRepository.existsByCode(color.getCode())) {
                throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã màu " + color.getCode() + " đã tồn tại");
            }
        }
        color.setDeleteFlag(false);
        return colorRepository.save(color);
    }

    @Override
    public Optional<Color> findById(Long id) {
        return colorRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        Color existingColor = colorRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy màu có id " + id) );
        existingColor.setDeleteFlag(true);
        colorRepository.save(existingColor);
    }

    @Override
    public List<Color> findAll() {
        return colorRepository.findAll();
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
