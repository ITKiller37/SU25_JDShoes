package com.example.jdshoes.controller.admin;

import com.example.jdshoes.dto.Bill.BillDtoInterface;
import com.example.jdshoes.dto.Product.ProductDto;
import com.example.jdshoes.repository.BillRepository;
import com.example.jdshoes.service.AccountService;
import com.example.jdshoes.service.BillService;
import com.example.jdshoes.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class  AdminHomeController {
    private final BillService billService;
    private final ProductService productService;

    private final BillRepository billRepository;

    private final AccountService accountService;

    public AdminHomeController(BillService billService, ProductService productService, BillRepository billRepository, AccountService accountService) {
        this.billService = billService;
        this.productService = productService;
        this.billRepository = billRepository;
        this.accountService = accountService;
    }

    @GetMapping("/admin")
    public String viewAdminHome(Model model) {
        Page<BillDtoInterface> billDtos = billService.findAll(Pageable.ofSize(10));
        Page<ProductDto> productDtos = productService.getAllProductApi(Pageable.ofSize(10));

        model.addAttribute("billList", billRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createDate"))));
//        model.addAttribute("totalBillQuantity", billDtos.getTotalElements());
        model.addAttribute("totalBillQuantity", billRepository.count());
        model.addAttribute("totalProduct", productDtos.getTotalElements());
        model.addAttribute("revenue", billRepository.calculateTotalRevenue());
        model.addAttribute("totalBillWaiting", billRepository.getTotalBillStatusWaiting());
        return "admin/home";
    }
}