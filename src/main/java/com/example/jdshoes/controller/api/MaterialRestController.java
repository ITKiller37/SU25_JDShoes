package com.example.jdshoes.controller.api;



import com.example.jdshoes.dto.Material.MaterialDto;
import com.example.jdshoes.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MaterialRestController {
    private final MaterialService materialService;

    @PostMapping("/api/material")
    public MaterialDto createMaterialApi(@RequestBody MaterialDto materialDto) {
        return materialService.createMaterialApi(materialDto);
    }
}
