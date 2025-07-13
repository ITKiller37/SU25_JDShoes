package com.example.jdshoes.controller.user;

import com.example.jdshoes.entity.Bill;
import com.example.jdshoes.entity.BillDetail;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.ProductDetail;
import com.example.jdshoes.entity.enumClass.BillStatus;
import com.example.jdshoes.entity.enumClass.InvoiceType;
import com.example.jdshoes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class BuyNowController {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;


    @RequestMapping("/buynow")
    public String viewPage(Model model, @RequestParam Long time, @RequestParam(name = "num-product") Integer numProduct ){
        ProductDetail productDetail = productDetailRepository.findById(time).orElse(null);

        model.addAttribute("productDetail", productDetail);
        model.addAttribute("numProduct", numProduct);
        model.addAttribute("totalAmount", productDetail.getPrice().doubleValue() * numProduct);
        return "user/buynow";
    }

    @PostMapping("/buynow")
    public String buyNowAction(@RequestParam String fullName, @RequestParam String phone,@RequestParam String email, @RequestParam String address, @RequestParam String note,
                               @RequestParam Long time, @RequestParam Integer quantity){
        Customer customer = customerRepository.findByAll(fullName, phone, email);
        Bill newBill = billRepository.findTopByOrderByIdDesc();
        if(customer == null){
            customer = new Customer();
            customer.setName(fullName);
            customer.setEmail(email);
            customer.setPhoneNumber(phone);
            customerRepository.save(customer);
            customer.setCode("KH00"+customer.getId().toString());
            customerRepository.save(customer);
        }
        ProductDetail productDetail = productDetailRepository.findById(time).get();
        Bill bill = new Bill();
        if(newBill == null){
            bill.setCode("HD001");
        }
        else{
            String numCodew = newBill.getCode().split("D")[1];
            bill.setCode("HD"+String.valueOf(Integer.valueOf(numCodew) + 1));
        }
        bill.setCustomer(customer);
        bill.setStatus(BillStatus.CHO_XAC_NHAN);
        bill.setAmount(productDetail.getPrice().multiply(new BigDecimal(quantity)));
        bill.setCreateDate(LocalDateTime.now());
        bill.setReturnStatus(false);
        bill.setInvoiceType(InvoiceType.ONLINE);
        bill.setBillingAddress(address);
        bill.setPaymentMethod(paymentMethodRepository.findById(1L).get());
        bill.setPromotionPrice(new BigDecimal(0));
        billRepository.save(bill);
        BillDetail billDetail = new BillDetail();
        billDetail.setProductDetail(productDetail);
        billDetail.setBill(bill);
        billDetail.setQuantity(quantity);
        billDetail.setDetailPrice(productDetail.getPrice());
        billDetailRepository.save(billDetail);
        return "redirect:/";
    }
}
