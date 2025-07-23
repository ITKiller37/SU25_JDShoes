package com.example.jdshoes.controller.user;

import com.example.jdshoes.dto.Account.AccountDto;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.AddressShipping;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.Role;
import com.example.jdshoes.entity.enumClass.RoleName;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.AddressShippingRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.repository.RoleRepository;
import com.example.jdshoes.service.AccountService;
import com.example.jdshoes.service.VerificationCodeService;
import com.example.jdshoes.utils.MailServices;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private MailServices mailServices;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressShippingRepository addressShippingRepository;

    @Autowired
    private VerificationCodeService verificationCodeService;

    private final CustomerRepository customerRepository;


    public AuthController(AccountService accountService, AccountRepository accountRepository, PasswordEncoder passwordEncoder, CustomerRepository customerRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/user-login")
    public String viewLogin(Model model) {

        return "user/login";
    }

    @GetMapping("/forgot-pass")
    public String forgotPass(Model model){

        return "user/forgot-pass";
    }

    @GetMapping("/register")
    public String register(Model model,@ModelAttribute("Account") Account account){
        return "user/register";
    }

    @PostMapping("/register-save")
    public String saveregister(Model model, @Validated @ModelAttribute AccountDto accountDto,
//                               MultipartFile chooseFile,
                               @RequestParam String tinh, @RequestParam String huyen, @RequestParam String xa, @RequestParam String tenduong,
                               RedirectAttributes redirectAttributes) throws MessagingException, IOException {
//        String avatar = null;
//        if(!chooseFile.isEmpty()){
//            avatar = "uploads/"+FileUploadUtil.saveFile("uploads",chooseFile.getOriginalFilename(),chooseFile);
//        }

        Account accountByEmail= accountService.findByEmail(accountDto.getEmail());

        Account accountByPhone = accountRepository.findByCustomer_PhoneNumber(accountDto.getPhoneNumber());

        if(accountByEmail !=null ){
            redirectAttributes.addFlashAttribute("errorMessage","Email đã tồn tại !");
            return "redirect:/register";
        }
        if(accountByPhone != null) {
            redirectAttributes.addFlashAttribute("errorMessage","Số điện thoại " + accountDto.getPhoneNumber() + " đã được đăng ký!");
            return "redirect:/register";
        }

        Account account = new Account();
        account.setEmail(accountDto.getEmail());
        Account account1 = accountRepository.findTopByOrderByIdDesc();
        Long nextCode = (account1 == null) ? 1 : account1.getId() + 1;
        String accCode = "TK" + String.format("%04d", nextCode);
        account.setCode(accCode);
//        account.setAvatar(avatar);
        String encoded = passwordEncoder.encode(accountDto.getPassword());
        account.setPassword(encoded);
        account.setName(accountDto.getName());
        account.setBirthDay(accountDto.getBod());
        account.setGender(accountDto.getGender());
        account.setNonLocked(true);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER);
        account.setRole(role);
        Customer customer = null;

        //Nếu số điện thoại đã tồn tại
        if(customerRepository.existsByPhoneNumber(accountDto.getPhoneNumber())) {
            customer = customerRepository.findByPhoneNumber(accountDto.getPhoneNumber());
            customer.setName(accountDto.getName());
        }
        else {
            customer = new Customer();
            customer.setName(accountDto.getName());
            customer.setPhoneNumber(accountDto.getPhoneNumber());
            Customer customerCurrent = customerRepository.findTopByOrderByIdDesc();
            Long nextCodeAcc = (customerCurrent == null) ? 1 : customerCurrent.getId() + 1;
            String productCode = "KH" + String.format("%04d", nextCodeAcc);
            customer.setCode(productCode);
            customer.setEmail(accountDto.getEmail());
            customer.setDeleted(false);
            customer.setGender(accountDto.getGender());
            customer.setBirthDay(accountDto.getBod());
        }
        account.setCustomer(customer);
        account.setCreateDate(LocalDateTime.now());

        customerRepository.save(customer);
        accountService.save(account);
        redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công");
        mailServices.sendEmail(accountDto.getEmail(),"Đăng ký tài khoản thành công",mailServices.buildEmailTemplate(account.getName(), accountDto.getEmail(), accountDto.getPassword()), false, true);

        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setStreet(tenduong);
        addressShipping.setWard(xa);
        addressShipping.setDistrict(huyen);
        addressShipping.setProvince(tinh);
        addressShipping.setAddress(tenduong+", "+xa+", "+huyen+", "+tinh);
        addressShipping.setCustomer(account.getCustomer());
        addressShippingRepository.save(addressShipping);
        return "redirect:/user-login";
    }

    @PostMapping("/reset-page")
    public String viewResetPassPage(@RequestParam String email, RedirectAttributes redirectAttributes) throws MessagingException {
        try {
            verificationCodeService.createVerificationCode(email);
        }
        catch (ShoesApiException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/forgot-pass";
        }
        return "redirect:/reset-pass";
    }

    @GetMapping("/reset-pass")
    public String viewResetPassPage() {
        return "user/reset-pass";
    }


    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String verificationCode,
                                @RequestParam String newPassword,
                                RedirectAttributes model) {
        // Kiểm tra mã xác nhận và lấy người dùng liên kết
        Account account = verificationCodeService.verifyCode(verificationCode);

        if (account != null) {
            // Đặt lại mật khẩu và xóa mã xác nhận
            accountService.resetPassword(account, newPassword);
            model.addFlashAttribute("success", "Đặt lại mật khẩu thành công");
            return "redirect:/user-login";
        } else {
            // Mã xác nhận không hợp lệ
            model.addFlashAttribute("errorMessage", "Mã xác thực không hợp lệ");
            return "redirect:/reset-pass";
        }
    }



}
