package com.example.jdshoes.controller.admin;


import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.enumClass.RoleName;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.repository.RoleRepository;
import com.example.jdshoes.utils.MailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailServices mailServices;

    @GetMapping("/customers")
    public String viewPage(Model model,@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "sort", defaultValue = "name,asc") String sortField, @RequestParam(required = false) String search){
        int pageSize = 5; // Number of items per page
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(sortDirection, sortFieldName);

        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<Customer> materialPage = null;
        if(search == null){
            materialPage = customerRepository.findAll(pageable);
        }
        else{
            materialPage = customerRepository.findByParam("%"+search+"%", pageable);
        }

        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);
        if(search != null){
            model.addAttribute("search", search);
        }
        else{
            model.addAttribute("search", "");
        }
        model.addAttribute("items", materialPage);
        return "admin/customer";
    }

    @GetMapping("/delete-customer")
    public String deleteCustomer(@RequestParam Long id){
        Customer customer = customerRepository.findById(id).get();
        customer.setDeleted(true);
        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/add-customer")
    public String addCustomerView(Model model, @RequestParam(required = false) Long id){
        if(id == null){
            model.addAttribute("customer", new Customer());
            model.addAttribute("account", new Account());
            model.addAttribute("title","Thêm khách hàng");
        }
        else{
            model.addAttribute("customer", customerRepository.findById(id).get());
            model.addAttribute("title","Cập nhật khách hàng");
            Account account = accountRepository.findByCustomer(id);
            if(account == null){
                model.addAttribute("account", new Account());
            }
            else{
                model.addAttribute("account", account);
            }
        }
        return "admin/addcustomer";
    }
    @PostMapping("/add-customer")
    public String addCustomerPost(@ModelAttribute Customer customer,
                                  @RequestParam String password,
                                  RedirectAttributes redirectAttributes) {

        if (customer.getId() == null) {
            // Check email trùng
            if (accountRepository.findByEmail(customer.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng");
                return "redirect:/admin/add-customer";
            }

            // Sinh mã khách hàng
            Customer lastCustomer = customerRepository.findTopByOrderByIdDesc();
            long nextCustomerId = (lastCustomer == null) ? 1 : lastCustomer.getId() + 1;
            String customerCode = "KH" + String.format("%04d", nextCustomerId);
            customer.setCode(customerCode);
            customer.setDeleted(false);
            customerRepository.save(customer);
            System.out.println("id customer: "+customer.getId());
            // Tạo mã tài khoản
            Account lastAccount = accountRepository.findTopByOrderByIdDesc();
            long nextAccountId = (lastAccount == null) ? 1 : lastAccount.getId() + 1;
            String accountCode = "TK" + String.format("%04d", nextAccountId);

            // Tạo và gán thông tin Account
            Account account = new Account();
            account.setCode(accountCode);
            account.setEmail(customer.getEmail());
            account.setPassword(passwordEncoder.encode(password));
            account.setCreateDate(LocalDateTime.now());
            account.setUpdateDate(LocalDateTime.now());
            account.setRole(roleRepository.findByRoleName(RoleName.ROLE_USER));
            account.setNonLocked(true);

            // Gán liên kết 2 chiều
            account.setCustomer(customer); // Hibernate cần cái này vì @JoinColumn(customer_id)
            customer.setAccount(account); // không bắt buộc nhưng nên có

            // Lưu account (Hibernate sẽ tự cascade customer)
            accountRepository.save(account);

            // Gửi email
            mailServices.sendEmail(
                    account.getEmail(),
                    "Thông tin tài khoản",
                    "Email đăng nhập hệ thống của bạn là: " + account.getEmail() +
                            "<br>Mật khẩu đăng nhập là: " + password,
                    false,
                    true
            );
        }

        return "redirect:/admin/customers";
    }


}
