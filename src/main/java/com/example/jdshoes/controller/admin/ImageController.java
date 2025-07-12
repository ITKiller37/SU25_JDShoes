package com.example.jdshoes.controller.admin;

import com.example.jdshoes.repository.ProductDetailRepository;
import com.example.jdshoes.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ProductDetailRepository productDetailRepository;


}
