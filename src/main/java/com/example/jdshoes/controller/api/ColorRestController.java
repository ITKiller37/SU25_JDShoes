package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Color.ColorDto;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ColorRestController {
    private final ColorService colorService;

    @GetMapping("/color/{productId}/product")
    public List<Color> getColorByProductId(@PathVariable Long productId) throws NotFoundException {
        return colorService.getColorByProductId(productId);
    }

    @GetMapping("/color/{productId}/product/{sizeId}/size")
    public List<Color> getColorByProductIdAndSizeId(@PathVariable Long productId, @PathVariable Long sizeId) throws NotFoundException {
        return colorService.getColorByProductIdAndSizeId(productId, sizeId);
    }
    @GetMapping("/colors/{productId}/product")
    public List<Color> getColorsByProductId(@PathVariable Long productId) throws NotFoundException {
        return colorService.getColorByProductId(productId);
    }

    @PostMapping("/api/color")
    public ColorDto createColorApi(@RequestBody ColorDto colorDto) {
        return colorService.createColorApi(colorDto);
    }
}
