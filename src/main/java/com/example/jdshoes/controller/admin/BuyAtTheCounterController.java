package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Product;
import com.example.jdshoes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuyAtTheCounterController {

    @Autowired
    private ProductService productService;

    @GetMapping("/admin/pos")
    public String getIndex(Model model) {
        Pageable able = PageRequest.of(0, 10);
        Page<Product> productPage = productService.getAllProduct(able);
        model.addAttribute("products", productPage);
        return "admin/buy_at_the_counter";
    }
}
