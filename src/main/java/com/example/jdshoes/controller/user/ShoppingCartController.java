package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.Account.AccountDto;
import com.example.jdshoes.dto.AddressShipping.AddressShippingDto;
import com.example.jdshoes.dto.Cart.CartItemDto;
import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.dto.Discount.DiscountDto;
import com.example.jdshoes.dto.Order.OrderDto;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.exception.NotFoundException;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.sercurity.CustomUserDetails;
import com.example.jdshoes.service.AddressShippingService;
import com.example.jdshoes.service.BillService;
import com.example.jdshoes.service.CartService;
import com.example.jdshoes.service.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@Transactional(readOnly = true)
public class ShoppingCartController {

    private final CartService cartService;

    private final BillService billService;

    private final DiscountService discountCodeService;

    private final AddressShippingService addressShippingService;

    private final AccountRepository accountRepository;

    public ShoppingCartController(CartService cartService, BillService billService, DiscountService discountCodeService, AddressShippingService addressShippingService, AccountRepository accountRepository) {
        this.cartService = cartService;
        this.billService = billService;
        this.discountCodeService = discountCodeService;
        this.addressShippingService = addressShippingService;
        this.accountRepository = accountRepository;
    }

    // Hiển thị trang giỏ hàng
    @GetMapping("/shoping-cart")
    public String viewShoppingCart(Model model, Authentication authentication) {

        CartDto cart = cartService.getCartByAccountId();
        Page<DiscountDto> discountCodeList = discountCodeService.getAllAvailableDiscountCode(PageRequest.of(0, 15));
        model.addAttribute("cart", cart); // Truyền một CartDto duy nhất
        model.addAttribute("discountCodes", discountCodeList.getContent());
        return "user/shoping-cart";
    }

    // Hiển thị trang thanh toán
    @GetMapping("/checkout")
    public String viewCheckout(Model model, Authentication authentication) {

        CartDto cart = cartService.getCartByAccountId();
        Page<DiscountDto> discountCodeList = discountCodeService.getAllAvailableDiscountCode(PageRequest.of(0, 15));
        model.addAttribute("cart", cart);
        model.addAttribute("discountCodes", discountCodeList.getContent());
        return "user/checkout";
    }

    // Thêm sản phẩm vào giỏ hàng
    @ResponseBody
    @PostMapping("/api/addToCart")
    public ResponseEntity<CartDto> addToCart(@RequestBody CartItemDto cartItemDto) {
        try {
            CartDto cartDto = cartService.addToCart(cartItemDto);
            return ResponseEntity.ok(cartDto);
        } catch (NotFoundException e) {
            throw new ShoesApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Thêm sản phẩm vào giỏ hàng và chuyển thẳng đến trang thanh toán (Mua ngay)
    @ResponseBody
    @PostMapping("/api/addToCartAndCheckout")
    public ResponseEntity<CartDto> addToCartAndCheckout(@RequestBody CartItemDto cartItemDto) {
        try {
            CartDto cartDto = cartService.addToCart(cartItemDto);
            return ResponseEntity.ok(cartDto);
        } catch (NotFoundException e) {
            throw new ShoesApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @ResponseBody
    @PostMapping("/api/deleteCart/{id}")
    public ResponseEntity<CartDto> deleteCart(@PathVariable Long id) {
        try {
            CartDto cartDto = cartService.removeFromCart(id);
            return ResponseEntity.ok(cartDto);
        } catch (NotFoundException e) {
            throw new ShoesApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @ResponseBody
    @PostMapping("/api/updateCart")
    public ResponseEntity<CartDto> updateCart(@RequestBody CartItemDto cartItemDto) {
        try {
            CartDto cartDto = cartService.updateCartItem(cartItemDto);
            return ResponseEntity.ok(cartDto);
        } catch (NotFoundException e) {
            throw new ShoesApiException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("loggedIn", false);
            return ResponseEntity.ok(response);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Account account = userDetails.getAccount();

        response.put("loggedIn", true);
        response.put("customerId", account.getCustomer() != null ? account.getCustomer().getId() : null);

        return ResponseEntity.ok(response);
    }


    @ResponseBody
    @PostMapping(value = "/api/complete-order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> completeOrder(@RequestBody OrderDto orderDto, @RequestParam String orderId) {
        try {
            cartService.completeOrderAfterPayment(orderDto, orderId);
            return ResponseEntity.ok("Hoàn tất đơn hàng thành công");
        } catch (NotFoundException | ShoesApiException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ResponseBody
    @Transactional(readOnly = true)
    @GetMapping("/api/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ShoesApiException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Account account = accountRepository.findByIdWithCustomer(userDetails.getAccount().getId())
                .orElseThrow(() -> new ShoesApiException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));
        Map<String, Object> response = new HashMap<>();

        if (account.getCustomer() == null) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Tài khoản không có thông tin khách hàng");
        }

        // Tạo AccountDto, ưu tiên lấy dữ liệu từ Customer nếu có, nếu không thì từ Account
        AccountDto accountDto = new AccountDto();
        accountDto.setName(account.getCustomer().getName() != null ? account.getCustomer().getName() : account.getName());
        accountDto.setPhoneNumber(account.getCustomer().getPhoneNumber() != null ? account.getCustomer().getPhoneNumber() : "");
        accountDto.setEmail(account.getCustomer().getEmail() != null ? account.getCustomer().getEmail() : account.getEmail());

        // Tạo CustomerDto
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(account.getCustomer().getId());
        customerDto.setName(account.getCustomer().getName());
        customerDto.setPhoneNumber(account.getCustomer().getPhoneNumber());
        customerDto.setEmail(account.getCustomer().getEmail());

        // Lấy địa chỉ mặc định
        AddressShippingDto defaultAddress = null;
        try {
            defaultAddress = addressShippingService.getDefaultAddressByCustomerId(account.getCustomer().getId());
        } catch (NotFoundException e) {
            defaultAddress = null; // Không có địa chỉ mặc định
        }

        response.put("account", accountDto);
        response.put("customer", customerDto);
        response.put("defaultAddress", defaultAddress);

        return ResponseEntity.ok(response);
    }


}

