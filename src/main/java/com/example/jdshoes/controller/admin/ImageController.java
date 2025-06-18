package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.SizeRepository;
import com.example.jdshoes.service.ColorService;
import com.example.jdshoes.service.ImageService;
import com.example.jdshoes.service.SizeService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;

    @GetMapping("/productDetail/{id}/images")
    public String getAllImagesByProductDetailId(@PathVariable Long productId,
                                                @RequestParam(required = false) Long sizeId,
                                                @RequestParam(required = false) Long colorId,
                                                Model model) throws NotFoundException {
        Size size = null;
        Color color = null;

        if (sizeId != null) {
            size = sizeRepository.findById(sizeId)
                    .orElseThrow(() -> new NotFoundException("Size not found with id: " + sizeId));
        }
        if (colorId != null) {
            color = colorRepository.findById(colorId)
                    .orElseThrow(() -> new NotFoundException("Color not found with id: " + colorId));
        }

        List<Image> images = imageService.getAllImagesByProductDetailId(productId, size, color);
        model.addAttribute("images", images);
        model.addAttribute("productId", productId);
        model.addAttribute("sizeId", sizeId);
        model.addAttribute("colorId", colorId);
        return "product-images";
    }
}
