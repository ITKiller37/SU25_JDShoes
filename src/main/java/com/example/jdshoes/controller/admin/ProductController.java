package com.example.jdshoes.controller.admin;



import com.example.jdshoes.dto.Product.CreateProductDetailsForm;
import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.service.*;
import com.example.jdshoes.utils.FileUploadUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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

    @Value("${upload.directory}")
    private String uploadDirectory;

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

    @GetMapping("/product-create")
    public String viewAddProduct(Model model, HttpSession session) {
        Product product = new Product();


        model.addAttribute("action", "/admin/product-create/save-part1");
        model.addAttribute("product", product);
        return "admin/product-create";
    }

    @PostMapping("/product-create/save-part1")
    public String handlePart1(@ModelAttribute("product") Product product, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (productService.existsByCode(product.getCode())) {
            redirectAttributes.addFlashAttribute("duplicateCode", "Mã sản phẩm đã tồn tại");
            return "redirect:/admin/product-create";
        }
        String randomString = UUID.randomUUID().toString();
        session.setAttribute("randomCreateKey", randomString);
        session.setAttribute("createProductPart1" + randomString, product);
        return "redirect:/admin/product-create/part2?key=" + randomString; // Thêm tham số key
    }

    @GetMapping("/product-create/part2")
    public String viewAddProductPart2(Model model, HttpSession session) {
        String randomCreateKey = (String) session.getAttribute("randomCreateKey");

        Product part1Data = (Product) session.getAttribute("createProductPart1" + randomCreateKey);
        if (part1Data == null) {
            // Nếu dữ liệu phần 1 không tồn tại, điều hướng người dùng trở lại phần 1
            return "redirect:/admin/product-create";
        }

        CreateProductDetailsForm createProductDetailsForm = new CreateProductDetailsForm();
        List<ProductDetail> productDetails = new ArrayList<>();
        productDetails.add(new ProductDetail());
        createProductDetailsForm.setProductDetailList(productDetails);
        model.addAttribute("form", createProductDetailsForm);
        return "/admin/product-create-detail";
    }

    @PostMapping("/product-save")
    @Transactional(rollbackOn = Exception.class)
    public String handlePart2(@ModelAttribute("form") CreateProductDetailsForm form,
                              HttpSession session,
                              // THAY ĐỔI LỚN TẠI ĐÂY:
                              // Sử dụng @RequestParam với tên cụ thể để chỉ lấy các file.
                              // Spring sẽ tự động ánh xạ multiple files (e.g., files[0][], files[0][])
                              // thành một List<MultipartFile> nếu chúng có cùng tên param.
                              @RequestParam(name = "files[0][]", required = false) List<MultipartFile> files0,
                              @RequestParam(name = "files[1][]", required = false) List<MultipartFile> files1,
                              @RequestParam(name = "files[2][]", required = false) List<MultipartFile> files2,
                              // ... tiếp tục thêm cho số lượng biến thể tối đa bạn dự kiến
                              // Hoặc một cách linh hoạt hơn (xem bên dưới)
                              RedirectAttributes redirectAttributes) throws IOException {
        // Kiểm tra dữ liệu từ phần 1 trong session
        String randomCreateKey = (String) session.getAttribute("randomCreateKey");
        Product part1Data = (Product) session.getAttribute("createProductPart1" + randomCreateKey);
        if (part1Data == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu sản phẩm không tồn tại, vui lòng thử lại.");
            return "redirect:/admin/product-create";
        }

        // Nhóm file theo index
        Map<Integer, List<MultipartFile>> detailFilesMap = new HashMap<>();

        // Thêm các file vào map thủ công
        if (files0 != null && !files0.isEmpty()) {
            detailFilesMap.put(0, files0);
        }
        if (files1 != null && !files1.isEmpty()) {
            detailFilesMap.put(1, files1);
        }
        if (files2 != null && !files2.isEmpty()) {
            detailFilesMap.put(2, files2);
        }
        // ... tiếp tục cho các index khác

        // Debug: In thông tin file nhận được
        detailFilesMap.forEach((index, fileList) -> {
            System.out.println("Files for variant " + index + ": " + fileList.size());
            fileList.forEach(file -> System.out.println("File name: " + file.getOriginalFilename()));
        });

        List<ProductDetail> productDetails = form.getProductDetailList();
        for (int i = 0; i < productDetails.size(); i++) {
            ProductDetail productDetail = productDetails.get(i);
            productDetail.setProduct(part1Data);

            // Xử lý ảnh cho từng biến thể
            List<Image> images = new ArrayList<>();
            List<MultipartFile> detailFiles = detailFilesMap.getOrDefault(i, new ArrayList<>());

            System.out.println("Processing variant " + i + " with " + detailFiles.size() + " files");

            for (MultipartFile file : detailFiles) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    String fileNameAfter = FileUploadUtil.saveFile(uploadDirectory, fileName, file);
                    Image image = new Image(null, fileNameAfter, LocalDateTime.now(), LocalDateTime.now(),
                            "uploads/" + fileNameAfter, file.getContentType(), productDetail);
                    images.add(image);
                    System.out.println("Added image: " + fileNameAfter + " for variant " + i);
                }
            }

            if (images.isEmpty()) {
                System.out.println("No images added for variant " + i);
            } else {
                System.out.println("Total images for variant " + i + ": " + images.size());
            }

            productDetail.setImages(images);

            System.out.println("Images set for ProductDetail " + i + ": " +
                    (productDetail.getImages() != null ? productDetail.getImages().size() : 0));
        }

        part1Data.setProductDetails(productDetails);
        System.out.println("Saving product with " + productDetails.size() + " variants");
        productService.save(part1Data);

        // Xóa dữ liệu session
        session.removeAttribute("randomCreateKey");
        session.removeAttribute("createProductPart1" + randomCreateKey);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm " + part1Data.getCode() + " thành công");
        return "redirect:/admin/product-all";
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
