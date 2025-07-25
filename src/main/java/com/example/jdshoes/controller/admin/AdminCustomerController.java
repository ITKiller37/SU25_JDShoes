package com.example.jdshoes.controller.admin;

import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.AddressShipping;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.entity.enumClass.RoleName;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.AddressShippingRepository;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.repository.RoleRepository;
import com.example.jdshoes.utils.MailServices;
import com.example.jdshoes.utils.RandomUtils;
import com.example.jdshoes.utils.VNCharacterUtils;
import jakarta.transaction.Transactional;
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
    @Autowired
    private AddressShippingRepository addressShippingRepository;

    @GetMapping("/customers")
    public String viewPage(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "sort", defaultValue = "name,asc") String sortField,
                           @RequestParam(required = false) String search) {

        int pageSize = 8;
        String[] sortParams = sortField.split(",");
        String sortFieldName = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, sortFieldName));
        Page<Customer> customerPage;

        if (search == null || search.trim().isEmpty()) {
            customerPage = customerRepository.findAllUserCustomers(pageable);
        } else {
            customerPage = customerRepository.findAllUserCustomersBySearch("%" + search.trim() + "%", pageable);
        }

        model.addAttribute("sortField", sortFieldName);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("items", customerPage);

        return "admin/customers";
    }


    @GetMapping("/delete-customer")
    public String deleteCustomer(@RequestParam Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            customer.setDeleted(true);
            customerRepository.save(customer);
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/add-customer")
    public String addCustomerView(Model model, @RequestParam(required = false) Long id) {
        if (id == null) {
            model.addAttribute("customer", new Customer());
            model.addAttribute("account", new Account());
            model.addAttribute("title", "Thêm khách hàng");
        } else {
            Customer customer = customerRepository.findById(id).orElse(null);
            model.addAttribute("customer", customer);
            model.addAttribute("title", "Cập nhật khách hàng");

            Account account = accountRepository.findByCustomer(id);
            model.addAttribute("account", account != null ? account : new Account());
        }
        return "admin/addcustomer";
    }

    @PostMapping("/add-customer")
    public String addCustomerPost(@ModelAttribute Customer customer,
                                  RedirectAttributes redirectAttributes,
                                  @RequestParam String tinh,
                                  @RequestParam String huyen,
                                  @RequestParam String xa,
                                  @RequestParam String tenduong) {

        // Kiểm tra trùng số điện thoại
        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã được sử dụng");
            return "redirect:/admin/add-customer";
        }

        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setStreet(tenduong);
        addressShipping.setWard(xa);
        addressShipping.setDistrict(huyen);
        addressShipping.setProvince(tinh);
        addressShipping.setAddress(tenduong + ", " + xa + ", " + huyen + ", " + tinh);
        addressShipping.setIsDefault(true);

        if (customer.getId() == null) {
            String passDecode = RandomUtils.randomPass();
            if (accountRepository.findByEmail(customer.getEmail()) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng");
                return "redirect:/admin/add-customer";
            }

            Customer lastCustomer = customerRepository.findTopByOrderByIdDesc();
            long nextCustomerId = (lastCustomer == null) ? 1 : lastCustomer.getId() + 1;
            customer.setCode("KH" + String.format("%04d", nextCustomerId));
            customer.setDeleted(false);
            customerRepository.save(customer);

            Account lastAccount = accountRepository.findTopByOrderByIdDesc();
            long nextAccountId = (lastAccount == null) ? 1 : lastAccount.getId() + 1;
            Account account = new Account();
            account.setCode("TK" + String.format("%04d", nextAccountId));
            account.setEmail(customer.getEmail());
            account.setPassword(passwordEncoder.encode(passDecode));
            account.setCreateDate(LocalDateTime.now());
            account.setUpdateDate(LocalDateTime.now());
            account.setRole(roleRepository.findByRoleName(RoleName.ROLE_USER));
            account.setNonLocked(true);
            account.setCustomer(customer);
            customer.setAccount(account);
            accountRepository.save(account);

            String content = VNCharacterUtils.buildAccountInfoEmail(account.getEmail(), passDecode);
            mailServices.sendEmail(account.getEmail(), "Thông tin tài khoản", content, false, true);

            addressShipping.setCustomer(account.getCustomer());
            addressShippingRepository.save(addressShipping);
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/customer/address/{id}/set-default")
    @Transactional
    public String setDefaultAddress(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AddressShipping addressShipping = addressShippingRepository.findById(id).orElse(null);
        if (addressShipping != null && addressShipping.getCustomer() != null) {
            Long customerId = addressShipping.getCustomer().getId();
            addressShippingRepository.updateAllIsDefaultFalseByCustomer(customerId);

            addressShipping.setIsDefault(true);
            addressShippingRepository.save(addressShipping);

            redirectAttributes.addFlashAttribute("successMessage", "Đã đặt địa chỉ mặc định thành công.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy địa chỉ cần đặt mặc định.");
        }
        return "redirect:/admin/customers";
    }



    @PostMapping("/customer/{customerId}/address/add")
    public String addAddressToCustomer(@PathVariable("customerId") Long customerId,
                                       @RequestParam(name = "province") String tinh,
                                       @RequestParam(name = "district") String huyen,
                                       @RequestParam(name = "ward") String xa,
                                       @RequestParam(name = "street") String tenduong,
                                       RedirectAttributes redirectAttributes) {

        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng.");
            return "redirect:/admin/customers";
        }

        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setStreet(tenduong);
        addressShipping.setWard(xa);
        addressShipping.setDistrict(huyen);
        addressShipping.setProvince(tinh);
        addressShipping.setAddress(tenduong + ", " + xa + ", " + huyen + ", " + tinh);
        addressShipping.setIsDefault(false); // Không đặt mặc định khi thêm nhanh
        addressShipping.setCustomer(customer);

        addressShippingRepository.save(addressShipping);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm địa chỉ cho khách hàng thành công!");

        return "redirect:/admin/customers";
    }

    @PostMapping("/add-address-customer")
    public String addAddress(@RequestParam Long idkh, @RequestParam String ward, @RequestParam String district,
                             @RequestParam String province, @RequestParam String detail, @RequestParam(required = false) Boolean isDefault){
        if(isDefault == null){
            isDefault = false;
        }
        Customer customer = customerRepository.findById(idkh).get();
        AddressShipping addressShipping = new AddressShipping();
        addressShipping.setAddress(detail+", "+ward+", "+district+", "+province);
        addressShipping.setCustomer(customer);
        addressShipping.setStreet(detail);
        addressShipping.setWard(ward);
        addressShipping.setDistrict(district);
        addressShipping.setProvince(province);
        addressShipping.setIsDefault(isDefault);

        if(isDefault != null && isDefault == true){
            addressShippingRepository.updateAllIsDefaultFalseByCustomer(idkh);
        }
        addressShippingRepository.save(addressShipping);
        return "redirect:/admin/customers";
    }
}
