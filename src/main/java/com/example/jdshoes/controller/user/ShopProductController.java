
package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.dto.Product.SearchProductDto;
import com.example.jdshoes.entity.Category;
import com.example.jdshoes.entity.Color;
import com.example.jdshoes.entity.Size;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ShopProductController {
    private final ProductService productService;
    private final SizeService sizeService;
    private final ColorService colorService;
    private final ProductDetailService productDetailService;
    private final CategoryService categoryService;
    public ShopProductController(ProductService productService, SizeService sizeService, ColorService colorService, ProductDetailService productDetailService, CategoryService categoryService) {
        this.productService = productService;
        this.sizeService = sizeService;
        this.colorService = colorService;
        this.productDetailService = productDetailService;
        this.categoryService = categoryService;
    }

    @GetMapping("/getproduct")
    public String getProduct(Model model, SearchProductDto searchProductDto, @PageableDefault(size = 18) Pageable pageable) {

        List<Category> categories = categoryService.getAll();
        Page<ProductDto> products = productService.searchProduct(searchProductDto, pageable);


        if(searchProductDto != null) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            Sort sort = pageable.getSort();
//            String url = "&size=" + pageSize;
            String url = "";


            if(searchProductDto.getKeyword() != null) {
                url += "&keyword=" + searchProductDto.getKeyword();
            }


            if(sort.isSorted()) {
                List<Sort.Order> orders = sort.toList();

                // Tạo một danh sách để lưu trữ chuỗi sắp xếp cho mỗi trường
                List<String> sortStrings = new ArrayList<>();

                for (Sort.Order order : orders) {
                    // Lấy tên trường (field)
                    String property = order.getProperty();

                    // Kiểm tra xem có phải là sắp xếp giảm dần không
                    boolean isDescending = order.isDescending();

                    // Tạo chuỗi sắp xếp dạng "field,asc" hoặc "field,desc"
                    String sortString = property + "," + (isDescending ? "desc" : "asc");

                    // Thêm chuỗi sắp xếp vào danh sách
                    sortStrings.add(sortString);
                }
                url += "&sort=" + String.join(",", sortStrings);
                searchProductDto.setSort(String.join(",", sortStrings));
            }

            if(searchProductDto.getMinPrice() != null) {
                url += "&minPrice=" + searchProductDto.getMinPrice();
            }
            if(searchProductDto.getMinPrice() != null) {
                url += "&maxPrice=" + searchProductDto.getMaxPrice();
            }
            if(searchProductDto.getCategoryId() != null) {
                url += "&category=" + searchProductDto.getCategoryId().stream()
                        .map(Object::toString) // Chuyển đổi mỗi số thành chuỗi
                        .collect(Collectors.joining(","));
            }
            model.addAttribute("url", url);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("dataFilter", searchProductDto);
        return "user/shop-product";
    }

    @GetMapping("/getproduct/search")
    public String getProductSearch(Model model, Pageable pageable, SearchProductDto searchDto) {
        Page<ProductDto> products = productService.searchProduct(searchDto, pageable);
        model.addAttribute("products", products);
        return "user/shop-product";
    }

    @GetMapping("/product-detail/{productCode}")
    public String getProductDetail(Model model, @PathVariable String productCode) {
        ProductDto product = productService.getProductDtoByCode(productCode);
        if (product == null) {
            return "/error/404";
        }

        Long productId = product.getId(); // Lấy id sản phẩm

        // Lấy size và color theo productId
        List<Size> sizes = sizeService.getSizesByProductIdH(productId);
        List<Color> colors = colorService.getColorsByProductId(productId);

        model.addAttribute("product", product);
        model.addAttribute("listSizes", sizes);
        model.addAttribute("listColors", colors);

        return "user/product-detail";
    }

    @ResponseBody
    @GetMapping("/productDetails/{productId}/product")
    public List<ProductDetailDto> getProductDetailJson(@PathVariable Long productId) throws NotFoundException {
        List<ProductDetailDto> productDetails = productDetailService.getByProductId(productId);
        return productDetails;
    }

}

