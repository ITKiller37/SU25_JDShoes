package com.example.jdshoes.service;


import com.example.jdshoes.dto.Customer.CustomerDto;
import com.example.jdshoes.dto.Customer.CustomerDtoApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomerService {
    Page<CustomerDto> getAllCustomers(Pageable pageable);

    CustomerDto createCustomerAdmin(CustomerDto customerDto);

    Page<CustomerDto> searchCustomerAdmin(String keyword, Pageable pageable);

    CustomerDtoApi getCustomerById(Long customerId);
}
