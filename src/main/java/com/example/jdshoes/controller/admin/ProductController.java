package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ColorService colorService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/product-all")
    public String searchAndListProducts(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createDate,desc") String sortField,
            @RequestParam(name = "maSanPham", required = false) String maSanPham,
            @RequestParam(name = "tenSanPham", required = false) String tenSanPham,
            @RequestParam(name = "nhanHang", required = false) Long nhanHang,
            @RequestParam(name = "chatLieu", required = false) Long chatLieu,
            @RequestParam(name = "theLoai", required = false) Long theLoai,
            @RequestParam(name = "trangThai", required = false) Integer trangThai) {

        int pageSize = 8;
        Sort sort;
        String sortFieldName = "createDate"; // Khai báo và khởi tạo mặc định

        try {
            String[] sortParams = sortField.split(",");
            sortFieldName = sortParams[0]; // Gán giá trị mới
            Sort.Direction sortDirection = Sort.Direction.ASC;

            if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
                sortDirection = Sort.Direction.DESC;
            }

            // Kiểm tra sortFieldName hợp lệ
            List<String> validFields = Arrays.asList("createDate", "name", "price"); // Thêm các trường hợp lệ
            if (!validFields.contains(sortFieldName)) {
                sortFieldName = "createDate"; // Mặc định nếu không hợp lệ
            }

            sort = Sort.by(sortDirection, sortFieldName);
        } catch (Exception e) {
            sort = Sort.by(Sort.Direction.DESC, "createDate"); // Mặc định nếu lỗi
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<ProductSearchDto> listProducts;

        // Kiểm tra tham số tìm kiếm
        boolean hasSearchParams = Stream.of(
                maSanPham != null && !maSanPham.trim().isEmpty() ? maSanPham : null,
                tenSanPham != null && !tenSanPham.trim().isEmpty() ? tenSanPham : null,
                nhanHang, chatLieu, theLoai, trangThai
        ).anyMatch(Objects::nonNull);

        try {
            if (hasSearchParams) {
                // Gửi null cho chuỗi rỗng
                maSanPham = maSanPham != null && maSanPham.trim().isEmpty() ? null : maSanPham;
                tenSanPham = tenSanPham != null && tenSanPham.trim().isEmpty() ? null : tenSanPham;
                listProducts = productService.listSearchProduct(maSanPham, tenSanPham, nhanHang, chatLieu, theLoai, trangThai, pageable);
            } else {
                listProducts = productService.getAll(pageable);
            }

            if (listProducts.isEmpty() && hasSearchParams) {
                model.addAttribute("message", "Không tìm thấy sản phẩm phù hợp.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi lấy danh sách sản phẩm.");
            listProducts = Page.empty(pageable);
        }

        model.addAttribute("maSanPham", maSanPham);
        model.addAttribute("tenSanPham", tenSanPham);
        model.addAttribute("nhanHang", nhanHang);
        model.addAttribute("chatLieu", chatLieu);
        model.addAttribute("theLoai", theLoai);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("sortDirection", sort.getOrderFor(sortFieldName) != null ? sort.getOrderFor(sortFieldName).getDirection() : Sort.Direction.DESC);
        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("items", listProducts);
        return "admin/product";
    }


    @ModelAttribute("listSize")
    public List<Size> getSize() {
        return sizeService.getAll();
    }

    @ModelAttribute("listColor")
    public List<Color> getColor() {
        return colorService.findAll();
    }

    @ModelAttribute("listBrand")
    public List<Brand> getBrand() {
        return brandService.getAll();
    }

    @ModelAttribute("listCategory")
    public List<Category> getCategory() {
        return categoryService.getAll();
    }

    @ModelAttribute("listMaterial")
    public List<Material> getMaterial() {
        return materialService.getAll();
    }
}
