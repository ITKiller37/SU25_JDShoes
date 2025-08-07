package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Customer.AddressDto;
import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.dto.Customer.CustomerDtoApi;
import com.example.jdshoes.entity.AddressShipping;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.repository.AddressShippingRepository;
import com.example.jdshoes.service.CustomerService;
import com.example.jdshoes.utils.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    private AddressShippingRepository addressShippingRepository;

    @Autowired
    private UserLoginUtil userLoginUtil;




    @GetMapping("/api/customer")
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerService.getAllCustomers(pageable);
    }

    @GetMapping("/api/customer/filter")
    public Page<CustomerDto> searchCustomers(@RequestParam String keyword,  Pageable pageable) {
        return customerService.searchCustomerAdmin(keyword, pageable);
    }

    @PostMapping("/customer/create")
    public CustomerDto createCustomerFormData(@ModelAttribute CustomerDto customerDto) {
        return customerService.createCustomerAdmin(customerDto);
    }

    @PostMapping("/api/customer")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.createCustomerAdmin(customerDto);
    }

    @GetMapping("/api/customer/{customerId}")
    public ResponseEntity<CustomerDtoApi> getCustomerById(@PathVariable Long customerId) {
        try {
            CustomerDtoApi customerDtoApi = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(customerDtoApi);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    @GetMapping("/api/customer/addresses")
    public ResponseEntity<List<AddressDto>> getCustomerAddresses() {
        Customer customer = userLoginUtil.getCurrentLogin().getCustomer();
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AddressShipping> addresses = addressShippingRepository.findByCustomerId(customer.getId());
        List<AddressDto> addressDtos = addresses.stream()
                .map(address -> new AddressDto(
                        address.getStreet(),
                        address.getWard(),
                        address.getDistrict(),
                        address.getProvince(),
                        address.getIsDefault()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addressDtos);
    }



}
