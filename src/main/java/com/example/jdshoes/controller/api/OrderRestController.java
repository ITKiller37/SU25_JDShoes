package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Cart.CartItemRequest;
import com.example.jdshoes.dto.Cart.CartItemResponse;
import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.dto.CartDto.ProductCart;
import com.example.jdshoes.dto.Image.ImageDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.dto.Product.ProductDetailDto;
import com.example.jdshoes.entity.*;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.*;
import com.example.jdshoes.service.CartService;
import com.example.jdshoes.utils.UserLoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderRestController {

    private final CartService cartService;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private UserLoginUtil userLoginUtil;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    // Đặt hàng trực tuyến
    @PostMapping("/orderUser")
    public ResponseEntity<?> orderUser(@RequestBody OrderDto orderDto, Authentication authentication) {

        try {
            Map<String, Object> response = cartService.orderUser(orderDto);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            throw new ShoesApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/orderAdmin")
    public ResponseEntity<OrderDto> createAdminOrder(@RequestBody OrderDto orderDto) {
        // Xử lý đơn hàng từ phía admin
        OrderDto createdOrder = cartService.orderAtCounter(orderDto);
        return ResponseEntity.ok(createdOrder);
    }

    @PostMapping("/getCartFromLocalStorage")
    public ResponseEntity<CartDto> getCartFromLocalStorage(@RequestBody List<CartItemRequest> cartItems) {
        try {
            CartDto cartDto = new CartDto();
            cartDto.setItems(new ArrayList<>());
            cartDto.setCreateDate(LocalDateTime.now());
            cartDto.setUpdatedDate(LocalDateTime.now());

            List<CartDto.CartItem> items = new ArrayList<>();

            for (CartItemRequest item : cartItems) {
                // Tìm ProductDetail
                ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + item.getProductDetailId()));

                // Tìm Product liên quan
                Product product = productRepository.findByProductDetailId(productDetail.getId());
                if (product == null || product.getStatus() == 2) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s - %s - %s đã ngừng bán",
                                    product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
                }

                // Kiểm tra số lượng tồn kho
                if (productDetail.getQuantity() < item.getQuantity()) {
                    throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                                    product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName(), productDetail.getQuantity()));
                }

                // Tạo ProductCart
                ProductCart productCart = new ProductCart();
                productCart.setProductId(product.getId());
                productCart.setName(product.getName());
                productCart.setCode(product.getCode());
                List<Image> images = productDetail.getImages();
                productCart.setImageUrl(images != null && !images.isEmpty() ? images.get(0).getLink() : null);

                // Tạo ProductDetailDto
                ProductDetailDto productDetailDto = new ProductDetailDto();
                productDetailDto.setId(productDetail.getId());
                productDetailDto.setProductId(product.getId());
                productDetailDto.setPrice(productDetail.getPrice());
                productDetailDto.setQuantity(productDetail.getQuantity());
                productDetailDto.setSizeName(productDetail.getSize().getName());
                productDetailDto.setColorName(productDetail.getColor().getName());
                productDetailDto.setProductName(product.getName());
                productDetailDto.setProductDescription(product.getDescription());
                productDetailDto.setBrandName(product.getBrand().getName());
                productDetailDto.setMaterialName(product.getMaterial().getName());
                productDetailDto.setCategoryName(product.getCategory().getName());

                // Thêm ảnh vào DTO
                List<ImageDto> imageDtos = images.stream()
                        .map(img -> {
                            ImageDto dto = new ImageDto();
                            dto.setLink(img.getLink());
                            dto.setId(img.getId());
                            return dto;
                        })
                        .collect(Collectors.toList());
                productDetailDto.setImages(imageDtos);

                // Nếu có khuyến mãi
                ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
                if (productDiscount != null) {
                    productDetailDto.setDiscountedAmount(productDiscount.getDiscountedAmount());
                }

                // Tạo CartItem
                CartDto.CartItem cartItem = new CartDto.CartItem();
                cartItem.setId(null); // Không có ID vì đây không phải từ database
                cartItem.setQuantity(item.getQuantity());
                cartItem.setCreateDate(LocalDateTime.now());
                cartItem.setUpdatedDate(LocalDateTime.now());
                cartItem.setProduct(productCart);
                cartItem.setProductDetail(productDetailDto);

                items.add(cartItem);
            }

            cartDto.setItems(items);
            return ResponseEntity.ok(cartDto);
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/mergeCart")
    @Transactional
    public List<CartItemResponse> mergeCart(@RequestBody List<CartItemRequest> guestCart) {
        // Lấy thông tin tài khoản hiện tại
        Account account = userLoginUtil.getCurrentLogin();
        if (account == null || account.getCustomer() == null) {
            throw new ShoesApiException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập hoặc tài khoản không hợp lệ");
        }
        Customer customer = account.getCustomer();

        // Lấy hoặc tạo giỏ hàng cho khách hàng
        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCreateDate(LocalDateTime.now());
                    newCart.setUpdateDate(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // Lấy danh sách CartDetail hiện tại
        List<CartDetail> existingCartDetails = cartDetailRepository.findByCartId(cart.getId());

        // Danh sách để lưu các mục giỏ hàng đã merge
        Map<Long, Integer> mergedCart = new HashMap<>();

        // Thêm các sản phẩm từ CartDetail hiện tại vào map
        for (CartDetail cartDetail : existingCartDetails) {
            mergedCart.put(cartDetail.getProductDetail().getId(), cartDetail.getQuantity());
        }

        // Merge giỏ hàng từ localStorage
        for (CartItemRequest item : guestCart) {
            ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + item.getProductDetailId()));
            Product product = productRepository.findByProductDetailId(productDetail.getId());
            if (product == null || product.getStatus() == 2) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Sản phẩm %s - %s - %s đã ngừng bán",
                                product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
            }

            int newQuantity = item.getQuantity();
            int currentQuantity = mergedCart.getOrDefault(productDetail.getId(), 0);
            int totalQuantity = currentQuantity + newQuantity;

            // Kiểm tra tồn kho
            if (totalQuantity > productDetail.getQuantity()) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Sản phẩm %s - %s - %s chỉ còn %d sản phẩm",
                                product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName(), productDetail.getQuantity()));
            }

            // Kiểm tra giới hạn số lượng (ví dụ: tối đa 20)
            if (totalQuantity > 20) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST,
                        String.format("Tổng số lượng của sản phẩm %s - %s - %s không được vượt quá 20",
                                product.getName(), productDetail.getSize().getName(), productDetail.getColor().getName()));
            }

            mergedCart.put(productDetail.getId(), totalQuantity);
        }

        // Cập nhật giỏ hàng trong database
        cartDetailRepository.deleteByCartId(cart.getId()); // Xóa các CartDetail cũ
        for (Map.Entry<Long, Integer> entry : mergedCart.entrySet()) {
            ProductDetail productDetail = productDetailRepository.findById(entry.getKey()).orElse(null);
            if (productDetail != null) {
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProductDetail(productDetail);
                cartDetail.setQuantity(entry.getValue());
                cartDetail.setCreateDate(LocalDateTime.now());
                cartDetail.setUpdateDate(LocalDateTime.now());
                cartDetailRepository.save(cartDetail);
            }
        }

        // Trả về giỏ hàng đã merge
        List<CartDetail> updatedCartDetails = cartDetailRepository.findByCartId(cart.getId());
        List<CartItemResponse> response = new ArrayList<>();
        for (CartDetail cartDetail : updatedCartDetails) {
            ProductDetail productDetail = cartDetail.getProductDetail();
            Product product = productRepository.findByProductDetailId(productDetail.getId());
            ProductDiscount productDiscount = productDiscountRepository.findValidDiscountByProductDetailId(productDetail.getId());
            BigDecimal price = productDiscount != null ? productDiscount.getDiscountedAmount() : productDetail.getPrice();

            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setProductDetailId(productDetail.getId());
            itemResponse.setQuantity(cartDetail.getQuantity());
            itemResponse.setProductName(product.getName());
            itemResponse.setSizeName(productDetail.getSize().getName());
            itemResponse.setColorName(productDetail.getColor().getName());
            itemResponse.setPrice(price);
            itemResponse.setImages(productDetail.getImages().stream().map(Image::getLink).collect(Collectors.toList()));
            response.add(itemResponse);
        }

        return response;
    }


//
//    @PostMapping("/check")
//    public ResponseEntity<List<CheckOrderDto>> checkOrderAvailability(@RequestBody List<CheckOrderDto> checkOrderDtoList) {
//        // Kiểm tra tính khả dụng của đơn hàng
//        List<CheckOrderDto> result = orderService.checkOrderAvailability(checkOrderDtoList);
//        return ResponseEntity.ok(result);
//    }

//    @GetMapping("/user/{customerId}")
//    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Long customerId) {
//        // Lấy danh sách đơn hàng của khách hàng
//        List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
//        return ResponseEntity.ok(orders);
//    }
}
