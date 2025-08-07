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
import org.springframework.util.MultiValueMap;
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
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "nhanHang", required = false) Long nhanHang,
            @RequestParam(name = "chatLieu", required = false) Long chatLieu,
            @RequestParam(name = "theLoai", required = false) Long theLoai,
            @RequestParam(name = "trangThai", required = false) Integer trangThai) {

        int pageSize = 8;
        Sort sort;
        String sortFieldName = "createDate";

        try {
            String[] sortParams = sortField.split(",");
            sortFieldName = sortParams[0];
            Sort.Direction sortDirection = Sort.Direction.ASC;

            if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
                sortDirection = Sort.Direction.DESC;
            }

            List<String> validFields = Arrays.asList("createDate", "name", "price");
            if (!validFields.contains(sortFieldName)) {
                sortFieldName = "createDate";
            }

            sort = Sort.by(sortDirection, sortFieldName);
        } catch (Exception e) {
            sort = Sort.by(Sort.Direction.DESC, "createDate");
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<ProductSearchDto> listProducts;

        boolean hasSearchParams = Stream.of(
                keyword,
                nhanHang, chatLieu, theLoai, trangThai
        ).anyMatch(Objects::nonNull);

        try {
            if (hasSearchParams) {
                keyword = keyword != null && keyword.trim().isEmpty() ? null : keyword;
                listProducts = productService.listSearchProduct(keyword, nhanHang, chatLieu, theLoai, trangThai, pageable);
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

        model.addAttribute("keyword", keyword);
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
        // Kiểm tra trùng tên sản phẩm
        if (productService.existsByName(product.getName())) {
            redirectAttributes.addFlashAttribute("duplicateName", "Tên sản phẩm đã tồn tại");
            return "redirect:/admin/product-create";
        }

        // Kiểm tra các trường bắt buộc
        if (product.getName() == null || product.getName().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm không được để trống");
            return "redirect:/admin/product-create";
        }

        if (product.getBrand() == null || product.getBrand().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Thương hiệu không được để trống");
            return "redirect:/admin/product-create";
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Loại sản phẩm không được để trống");
            return "redirect:/admin/product-create";
        }

        if (product.getMaterial() == null || product.getMaterial().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Chất liệu không được để trống");
            return "redirect:/admin/product-create";
        }

        String randomString = UUID.randomUUID().toString();
        session.setAttribute("randomCreateKey", randomString);
        session.setAttribute("createProductPart1" + randomString, product);
        return "redirect:/admin/product-create/part2?key=" + randomString;
    }

    @GetMapping("/product-create/part2")
    public String viewAddProductPart2(Model model, HttpSession session) {
        String randomCreateKey = (String) session.getAttribute("randomCreateKey");
        Product part1Data = (Product) session.getAttribute("createProductPart1" + randomCreateKey);
        if (part1Data == null) {
            return "redirect:/admin/product-create";
        }

        CreateProductDetailsForm createProductDetailsForm = new CreateProductDetailsForm();
        List<ProductDetail> productDetails = new ArrayList<>();
        createProductDetailsForm.setProductDetailList(productDetails);
        model.addAttribute("form", createProductDetailsForm);
        return "/admin/product-create-detail";
    }

    @PostMapping("/product-save")
    @Transactional(rollbackOn = Exception.class)
    public String handlePart2(@ModelAttribute("form") CreateProductDetailsForm form,
                              HttpSession session,
                              @RequestParam MultiValueMap<String, MultipartFile> allRequestParams, // Thay đổi ở đây!
                              RedirectAttributes redirectAttributes) throws IOException {
        String randomCreateKey = (String) session.getAttribute("randomCreateKey");
        Product part1Data = (Product) session.getAttribute("createProductPart1" + randomCreateKey);
        if (part1Data == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu sản phẩm không tồn tại, vui lòng thử lại.");
            return "redirect:/admin/product-create";
        }

        // Nhóm file theo colorId từ allRequestParams
        Map<Long, List<MultipartFile>> colorFilesMap = new HashMap<>();
        // Lặp qua các entry của MultiValueMap
        for (Map.Entry<String, List<MultipartFile>> entry : allRequestParams.entrySet()) { // entry.getValue() bây giờ là List<MultipartFile>
            String paramName = entry.getKey();
            List<MultipartFile> filesForParam = entry.getValue(); // Danh sách các file cho cùng một paramName

            // Kiểm tra và trích xuất colorId
            if (paramName.startsWith("colorFiles[") && paramName.endsWith("[]")) {
                String colorIdStr = paramName.replaceAll("colorFiles\\[(\\d+)\\]\\[\\]", "$1");
                try {
                    Long colorId = Long.parseLong(colorIdStr);
                    // Thêm tất cả các file từ filesForParam vào colorFilesMap
                    for (MultipartFile file : filesForParam) {
                        if (!file.isEmpty()) {
                            colorFilesMap.computeIfAbsent(colorId, k -> new ArrayList<>()).add(file);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid colorId in file param: " + paramName);
                }
            }
        }

        // Debug: In thông tin file nhận được
        if (colorFilesMap.isEmpty()) {
            System.out.println("No color files received in colorFilesMap.");
        } else {
            colorFilesMap.forEach((colorId, fileList) -> {
                System.out.println("Files for color " + colorId + ": " + fileList.size());
                fileList.forEach(file -> System.out.println("File name: " + file.getOriginalFilename() + ", Field name: " + file.getName()));
            });
        }

        // ... (Phần còn lại của logic xử lý vẫn giữ nguyên) ...

        List<ProductDetail> productDetails = form.getProductDetailList();
        Map<Long, List<Image>> colorImagesMap = new HashMap<>();

        // Ánh xạ colorId và sizeId thành Color và Size (Giữ nguyên logic này)
        for (ProductDetail productDetail : productDetails) {
            if (productDetail.getColorId() != null) {
                Color color = colorService.findById(productDetail.getColorId()).orElse(null);
                productDetail.setColor(color);
            }
            if (productDetail.getSizeId() != null) {
                Size size = sizeService.findById(productDetail.getSizeId()).orElse(null);
                productDetail.setSize(size);
            }
        }

        // Xử lý ảnh cho từng màu
        for (Long colorId : colorFilesMap.keySet()) {
            List<MultipartFile> files = colorFilesMap.getOrDefault(colorId, new ArrayList<>());
            List<Image> images = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    String fileNameAfter = FileUploadUtil.saveFile(uploadDirectory, fileName, file);
                    Image image = new Image(null, fileNameAfter, LocalDateTime.now(), LocalDateTime.now(),
                            "uploads/" + fileNameAfter, file.getContentType(), null);
                    images.add(image);
                    System.out.println("Added image: " + fileNameAfter + " for color " + colorId);
                }
            }
            if (!images.isEmpty()) {
                colorImagesMap.put(colorId, images);
            }
        }

        // Gán ảnh cho các biến thể theo màu
        for (int i = 0; i < productDetails.size(); i++) {
            ProductDetail productDetail = productDetails.get(i);
            productDetail.setProduct(part1Data);
            Long colorId = productDetail.getColor() != null ? productDetail.getColor().getId() : null;
            if (colorId == null) {
                System.out.println("Warning: Color is null for productDetail at index " + i);
                continue;
            }
            List<Image> images = colorImagesMap.getOrDefault(colorId, new ArrayList<>());
            List<Image> productDetailImages = new ArrayList<>();
            for (Image image : images) {
                Image imageCopy = new Image();
                imageCopy.setName(image.getName());
                imageCopy.setCreateDate(image.getCreateDate());
                imageCopy.setUpdateDate(image.getUpdateDate());
                imageCopy.setLink(image.getLink());
                imageCopy.setFileType(image.getFileType());
                imageCopy.setProductDetail(productDetail);
                productDetailImages.add(imageCopy);
            }
            productDetail.setImages(productDetailImages);
            System.out.println("Assigned " + productDetail.getImages().size() + " images to variant " + i + " (color: " + colorId + ")");
        }

        part1Data.setProductDetails(productDetails);
        System.out.println("Saving product with " + productDetails.size() + " variants");
        productService.save(part1Data);
        productService.updateProductStatusBasedOnQuantity(part1Data);

        session.removeAttribute("randomCreateKey");
        session.removeAttribute("createProductPart1" + randomCreateKey);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm " + part1Data.getCode() + " thành công");
        return "redirect:/admin/product-all";
    }

    @PostMapping("/product-toggle-status")
    public String toggleProductStatus(
            @RequestParam("maSanPham") String maSanPham,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findByCode(maSanPham);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm với mã " + maSanPham);
                return "redirect:/admin/product-all";
            }

            Integer currentStatus = product.getStatus();
            Integer newStatus = (currentStatus != null && currentStatus == 1) ? 2 : 1;

            product.setStatus(newStatus);
            productService.save(product);

            String statusText = newStatus == 1 ? "Còn bán" : "Ngừng bán";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái sản phẩm " + maSanPham + " thành " + statusText + " thành công");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi cập nhật trạng thái sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/product-all";
    }

    @ModelAttribute("listSize")
    public List<Size> getSize() {
        return sizeService.getAllActive();
    }

    @ModelAttribute("listColor")
    public List<Color> getColor() {
        return colorService.getAllActive();
    }

    @ModelAttribute("listBrand")
    public List<Brand> getBrand() {
        return brandService.getAllActive();
    }

    @ModelAttribute("listCategory")
    public List<Category> getCategory() {
        return categoryService.getAllActive();
    }

    @ModelAttribute("listMaterial")
    public List<Material> getMaterial() {
        return materialService.getAllActive();
    }


}