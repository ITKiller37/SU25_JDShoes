package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.repository.ColorRepository;
import com.example.jdshoes.repository.ProductDetailRepository;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ProductDetailRepository productDetailRepository;


}
