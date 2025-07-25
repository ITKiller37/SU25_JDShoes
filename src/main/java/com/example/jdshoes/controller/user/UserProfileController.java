package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.Account.AccountDto;
import com.example.jdshoes.dto.Account.ChangePasswordDto;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.AddressShipping;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.repository.AddressShippingRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.service.AccountService;
import com.example.jdshoes.utils.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserProfileController {
    private final AccountService accountService;

    @Autowired
    private AddressShippingRepository addressShippingRepository;

    @Autowired
    private UserLoginUtil userLoginUtil;

    @Autowired
    private CustomerRepository customerRepository;

    public UserProfileController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public String viewProfilePage(Model model) {
        AccountDto accountDto = accountService.getAccountLogin();
        model.addAttribute("profile", accountDto);
//        model.addAttribute("profile", new AccountDto());
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(AccountDto accountDto, RedirectAttributes redirectAttributes) {
         try {
             accountService.updateProfile(accountDto);
             redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công");
         }
         catch (Exception e) {
             redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
         }
         return "redirect:/profile";
    }

    @PostMapping("/add-address")
    public String addAddress(@RequestParam String tinh, @RequestParam String huyen, @RequestParam String xa, @RequestParam String tenduong
                             ,@RequestParam(required = false) Boolean defaultAdd
            , RedirectAttributes redirectAttributes) {
        Account account = userLoginUtil.getCurrentLogin();
        Customer customer = customerRepository.findByAccount_Id(account.getId());
        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setStreet(tenduong);
        addressShipping.setWard(xa);
        addressShipping.setDistrict(huyen);
        addressShipping.setProvince(tinh);
        if(defaultAdd != null && defaultAdd == true){
            addressShipping.setIsDefault(defaultAdd);
        }
        addressShipping.setAddress(tenduong+", "+xa+", "+huyen+", "+tinh);
        addressShipping.setCustomer(customer);
        addressShippingRepository.save(addressShipping);
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(ChangePasswordDto changePasswordDto, RedirectAttributes redirectAttributes) {
        try {
            accountService.changePassword(changePasswordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mật khẩu thành công");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/setDefaultAddress")
    public String setDefaultAddress(@RequestParam Long id) {
        Account account = userLoginUtil.getCurrentLogin();
        Customer customer = customerRepository.findByAccount_Id(account.getId());
        addressShippingRepository.resetDefaultAddress(customer.getId());

        AddressShipping addressShipping = addressShippingRepository.findById(id).get();
        addressShipping.setIsDefault(true);
        addressShippingRepository.save(addressShipping);
        return "redirect:/profile";
    }
}
