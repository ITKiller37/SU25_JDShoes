package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Size.SizeDto;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SizeRestController {
    private final SizeService sizeService;

    @GetMapping("/sizes/{productId}/product")
    public List<Size> getColorsByProductId(@PathVariable Long productId) throws NotFoundException {
        return sizeService.getSizesByProductId(productId);
    }

    @GetMapping("/sizes/{productId}/product/{colorId}/color")
    public List<Size> getSizesByProductIdAndColorId(@PathVariable Long productId, @PathVariable Long colorId) throws NotFoundException {
        return sizeService.getSizesByProductIdAndColorId(productId, colorId);
    }

    @PostMapping("/api/size")
    public SizeDto createSizeApi(@RequestBody SizeDto sizeDto) {
        return sizeService.createSizeApi(sizeDto);
    }

}
