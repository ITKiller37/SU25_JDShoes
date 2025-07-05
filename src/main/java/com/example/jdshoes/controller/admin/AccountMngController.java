package com.example.jdshoes.controller.admin;


import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.AddressShipping;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.enumClass.RoleName;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.AddressShippingRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.repository.RoleRepository;
import com.example.jdshoes.service.AccountService;
import com.example.jdshoes.utils.MailServices;
import com.example.jdshoes.utils.RandomUtils;
import com.example.jdshoes.utils.VNCharacterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;
import java.util.List;

@Controller
public class AccountMngController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressShippingRepository addressShippingRepository;

    @Autowired
    private MailServices mailServices;

    public AccountMngController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/admin-only/account-management")
    public String getAccountManagement(@RequestParam(defaultValue = "") String search,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountPage;

        if (search.trim().isEmpty()) {
            accountPage = accountService.findByRoleWithPaging(RoleName.ROLE_EMPLOYEE, pageable);
        } else {
            accountPage = accountService.searchEmployeeByEmailOrName(RoleName.ROLE_EMPLOYEE, search, pageable);
        }

        model.addAttribute("accountList", accountPage);
        model.addAttribute("search", search);
        return "admin/account";
    }


    @GetMapping("/admin-only/addacount")
    public String addAcountPage(Model model, @RequestParam(required = false) Long id) {
        System.out.println("Error in model: " + model.asMap().get("error"));
        if(id != null){
            model.addAttribute("account", accountRepository.findById(id).get());
        }
        else{
            model.addAttribute("account", new Account());
        }
        return "admin/addaccount";
    }

    @PostMapping("/admin-only/addacount")
    public String postAccount(@ModelAttribute Account account, @RequestParam String name, @RequestParam String phone,
                              RedirectAttributes redirectAttributes,@RequestParam String tinh, @RequestParam String huyen, @RequestParam String xa, @RequestParam String tenduong){
        account.setRole(roleRepository.findByRoleName(RoleName.ROLE_EMPLOYEE));
        String passDecode = RandomUtils.randomPass();
        if(isAtLeast15YearsOld(account.getBirthDay()) == false){
            redirectAttributes.addFlashAttribute("errorBirth", "Tuổi phải đủ 15!");
            return "redirect:/admin-only/addacount";
        }
        if(account.getId() == null){
            if(accountRepository.findByEmail(account.getEmail()) != null){
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                return "redirect:/admin-only/addacount";
            }
            Customer customerCurrent = customerRepository.findTopByOrderByIdDesc();
            Long nextCodeAcc = (customerCurrent == null) ? 1 : customerCurrent.getId() + 1;
            String productCode = "KH" + String.format("%04d", nextCodeAcc);
            Account account1 = accountRepository.findTopByOrderByIdDesc();
            Long nextCode = (account1 == null) ? 1 : account1.getId() + 1;
            String accCode = "TK" + String.format("%04d", nextCode);
            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhoneNumber(phone);
            customer.setCode(productCode);
            customerRepository.save(customer);
            account.setCustomer(customer);
            account.setPassword(passwordEncoder.encode(passDecode));
            account.setCreateDate(LocalDateTime.now());
            account.setNonLocked(true);
            account.setCode(accCode);
        }
        else{
            if(accountRepository.findByEmail(account.getEmail(), account.getId()) != null){
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                return "redirect:/admin-only/addacount";
            }
            Account ex = accountRepository.findById(account.getId()).get();
            ex.getCustomer().setName(name);
            ex.getCustomer().setPhoneNumber(phone);
            customerRepository.save(ex.getCustomer());
            account.setCode(ex.getCode());
            account.setUpdateDate(LocalDateTime.now());
            account.setCreateDate(ex.getCreateDate());
            account.setCustomer(ex.getCustomer());
            account.setNonLocked(ex.isNonLocked());
        }
        accountRepository.save(account);
        String content = VNCharacterUtils.buildAccountInfoEmail(account.getEmail(), passDecode);
        mailServices.sendEmail(account.getEmail(), "Thông tin tài khoản",
                content,
                false, true);
        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setStreet(tenduong);
        addressShipping.setWard(xa);
        addressShipping.setDistrict(huyen);
        addressShipping.setProvince(tinh);
        addressShipping.setAddress(tenduong+", "+xa+", "+huyen+", "+tinh);
        addressShipping.setCustomer(account.getCustomer());
        addressShippingRepository.save(addressShipping);
        return "redirect:/admin-only/account-management";
    }

    @PostMapping("/account/block/{id}")
    public String blockAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.blockAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã khóa thành công");
        return "redirect:/admin-only/account-management";
    }

    @PostMapping("/account/open/{id}")
    public String openAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.openAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã mở khóa thành công");
        return "redirect:/admin-only/account-management";
    }

    @PostMapping("/account/change-role")
    public String openAccount(@ModelAttribute("email") String email, @ModelAttribute("role") Long roleId, RedirectAttributes redirectAttributes) {
        Account account = accountService.changeRole(email, roleId);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã đổi thành quyền thành công");
        return "redirect:/admin-only/account-management";
    }


    public static boolean isAtLeast15YearsOld(Date date) {
        if (date == null) return false;
        java.sql.Date birthDate = new java.sql.Date(date.getTime());
        LocalDate birthLocalDate = birthDate.toLocalDate();
        LocalDate today = LocalDate.now();

        Period age = Period.between(birthLocalDate, today);

        return age.getYears() > 15 || (age.getYears() == 15 &&
                (age.getMonths() > 0 || age.getDays() >= 0));
    }
}
