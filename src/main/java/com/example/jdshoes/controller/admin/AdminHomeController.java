package com.example.jdshoes.controller.admin;



import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminHomeController {

    private final ProductService productService;

    @GetMapping("/admin")
    public String viewAdminHome(Model model) {

        Page<ProductDto> productDtos = productService.getAllProductApi(Pageable.ofSize(10));
        model.addAttribute("totalProduct", productDtos.getTotalElements());
        return "/admin/home";
    }
}
