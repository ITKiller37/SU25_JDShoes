package com.example.jdshoes.service;

import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Image;
import com.example.jdshoes.entity.Size;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageService {

    List<Image> getAllImagesByProductDetailId(Long productDetailId);
}
