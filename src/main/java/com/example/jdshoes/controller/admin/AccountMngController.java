package com.example.jdshoes.controller.admin;



import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AccountMngController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerRepository customerRepository;

    public AccountMngController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/admin-only/account-management")
    public String viewAccountManagementPage(Model model) {
        List<Account> accountList = accountService.findAllAccount();
        model.addAttribute("accountList", accountList);
        return "admin/account";
    }

    @GetMapping("/admin-only/addaccount")
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

    @PostMapping("/admin-only/addaccount")
    public String postAccount(@ModelAttribute Account account, @RequestParam String name, @RequestParam String phone,
                              RedirectAttributes redirectAttributes){
        if(account.getId() == null){
            if(accountRepository.findByEmail(account.getEmail()) != null){
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                System.out.println("Tài khoản đã được sử dụng");
                return "redirect:/admin-only/addaccount";
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
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreateDate(LocalDateTime.now());
            account.setNonLocked(true);
            account.setCode(accCode);
        }
        else{
            if(accountRepository.findByEmail(account.getEmail(), account.getId()) != null){
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
                return "redirect:/admin-only/addaccount";
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
}
