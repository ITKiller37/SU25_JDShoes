package com.example.jdshoes.service.serviceImpl;



import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Product;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShopApiException;
import com.example.jdshoes.repository.ColorRepository;
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

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public SizeDto createSizeApi(SizeDto sizeDto) {

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
        if(sizeRepository.existsByCode(size.getCode())) {
            throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã cỡ " + size.getCode() + " đã tồn tại");
        }

        size.setDeleteFlag(false);
        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(Size size) {
        Size existingSize = sizeRepository.findById(size.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy cỡ có mã " + size.getCode()) );
        if(!existingSize.getCode().equals(size.getCode())) {
            if(sizeRepository.existsByCode(size.getCode())) {
                throw new ShopApiException(HttpStatus.BAD_REQUEST, "Mã cỡ " + size.getCode() + " đã tồn tại");
            }
        }
        size.setDeleteFlag(false);
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
}
