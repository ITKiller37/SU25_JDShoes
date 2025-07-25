package com.example.jdshoes.controller.api;

import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.dto.Customer.CustomerDtoApi;

import com.example.jdshoes.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }


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


}
