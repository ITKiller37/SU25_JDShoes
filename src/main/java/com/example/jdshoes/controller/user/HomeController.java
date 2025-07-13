package com.example.jdshoes.controller.user;


import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.service.CategoryService;
import com.example.jdshoes.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String gethome(Model model, SearchProductDto searchProductDto,
                          @PageableDefault(size = 20, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        List<Category> categories = categoryService.getAll();
        Page<ProductDto> products = productService.searchProduct(searchProductDto, pageable);

        // Tạo URL giữ lại các filter
        StringBuilder url = new StringBuilder();

        if (searchProductDto.getKeyword() != null) {
            url.append("&keyword=").append(searchProductDto.getKeyword());
        }

        if (searchProductDto.getMinPrice() != null) {
            url.append("&minPrice=").append(searchProductDto.getMinPrice());
        }

        if (searchProductDto.getMaxPrice() != null) {
            url.append("&maxPrice=").append(searchProductDto.getMaxPrice());
        }

        if (searchProductDto.getCategoryId() != null && !searchProductDto.getCategoryId().isEmpty()) {
            url.append("&categoryId=").append(searchProductDto.getCategoryId().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
        }

        if (searchProductDto.getBrandName() != null) {
            url.append("&brandName=").append(searchProductDto.getBrandName());
        }

        if (searchProductDto.getSort() != null) {
            url.append("&sort=").append(searchProductDto.getSort());
        } else {
            Sort sort = pageable.getSort();
            if (sort.isSorted()) {
                String sortStr = sort.toList().stream()
                        .map(order -> order.getProperty() + "," + (order.isDescending() ? "desc" : "asc"))
                        .collect(Collectors.joining(","));
                url.append("&sort=").append(sortStr);
            }
        }

        model.addAttribute("url", url.toString());
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("dataFilter", searchProductDto);

        return "user/home-03";
    }
}
