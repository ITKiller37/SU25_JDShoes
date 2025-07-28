package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.CartDto.CartDto;
import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.service.BillService;
import com.example.jdshoes.service.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
public class OrderStatusController {


    private final CartService cartService;
    private final BillService billService;

    public OrderStatusController(CartService cartService, BillService billService) {

        this.cartService = cartService;
        this.billService = billService;
    }

    @ResponseBody
    @GetMapping("/api/getCart")
    public ResponseEntity<CartDto> getCart() {
        try {
            CartDto cartDto = cartService.getCartByAccountId();
            return ResponseEntity.ok(cartDto);
        } catch (ShoesApiException e) {
            throw new ShoesApiException(e.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/cart-status-account")
    public String viewCartStatus(Model model,
                                 @RequestParam(required = false) String status,
                                 @PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Bill> billPage = null;
        if(status == null || status.trim().isEmpty()) {
            billPage = billService.getBillByAccount(pageable);
        }else {
            billPage = billService.getBillByStatus(status, pageable);
            model.addAttribute("status", status);
        }

        model.addAttribute("bills", billPage);
        return "user/cart-status-account";
    }

    @PostMapping("/cancel-bill/{id}")
    public String cancelBill(@PathVariable Long id) {
        billService.updateStatus("HUY", id);
        return "redirect:/cart-status-account";
    }
}
