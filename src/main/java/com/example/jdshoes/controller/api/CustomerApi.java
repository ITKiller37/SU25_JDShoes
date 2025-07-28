package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Customer.AddressDto;
import com.example.jdshoes.dto.Customer.CustomerDtoApi;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/postAddCustomer")
public class CustomerApi {

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

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDtoApi customerDto) {
        // Kiểm tra trùng email
        if (accountRepository.findByEmail(customerDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã được sử dụng"));
        }

        // Kiểm tra trùng số điện thoại
        if (customerRepository.existsByPhoneNumber(customerDto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã được sử dụng"));
        }

        // Tạo khách hàng mới
        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setEmail(customerDto.getEmail());
        customer.setGender(customerDto.getGender());
        customer.setBirthDay(customerDto.getBirthDay());
        customer.setDeleted(false);

        // Tạo mã khách hàng
        Customer lastCustomer = customerRepository.findTopByOrderByIdDesc();
        long nextCustomerId = (lastCustomer == null) ? 1 : lastCustomer.getId() + 1;
        String customerCode = "KH" + String.format("%04d", nextCustomerId);

        // Kiểm tra trùng mã khách hàng
        if (customerRepository.existsByCode(customerCode)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã khách hàng đã được sử dụng"));
        }

        customer.setCode(customerCode);
        customer = customerRepository.save(customer);

        // Tạo tài khoản
        String password = RandomUtils.randomPass();

        Account lastAccount = accountRepository.findTopByOrderByIdDesc();
        long nextAccountId = (lastAccount == null) ? 1 : lastAccount.getId() + 1;
        Account account = new Account();
        account.setCode("TK" + String.format("%04d", nextAccountId));
        account.setEmail(customer.getEmail());
        account.setPassword(passwordEncoder.encode(password));
        account.setCreateDate(LocalDateTime.now());
        account.setUpdateDate(LocalDateTime.now());
        account.setRole(roleRepository.findByRoleName(RoleName.ROLE_USER));
        account.setNonLocked(true);
        account.setCustomer(customer);
        account = accountRepository.save(account);

        // Gửi email thông báo tài khoản
        String content = VNCharacterUtils.buildAccountInfoEmail(account.getEmail(), password);
        mailServices.sendEmail(account.getEmail(), "Thông tin tài khoản", content, false, true);

        // Xử lý danh sách địa chỉ
        if (customerDto.getAddress() != null && !customerDto.getAddress().isEmpty()) {
            for (AddressDto addressDto : customerDto.getAddress()) {
                AddressShipping addressShipping = new AddressShipping();
                addressShipping.setStreet(addressDto.getStreet());
                addressShipping.setWard(addressDto.getWard());
                addressShipping.setDistrict(addressDto.getDistrict());
                addressShipping.setProvince(addressDto.getProvince());
                addressShipping.setAddress(
                        addressDto.getStreet() + ", " +
                                addressDto.getWard() + ", " +
                                addressDto.getDistrict() + ", " +
                                addressDto.getProvince()
                );
                addressShipping.setIsDefault(addressDto.isDefault());
                addressShipping.setCustomer(customer);
                addressShippingRepository.save(addressShipping);
            }
        }

        return ResponseEntity.ok(customer);
    }
}
