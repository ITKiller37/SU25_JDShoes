package com.example.jdshoes.service;


import com.example.jdshoes.dto.CustomerDto.CustomerDto;
import com.example.jdshoes.dto.CustomerDto.CustomerDtoApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomerService {
    Page<CustomerDto> getAllCustomers(Pageable pageable);

    CustomerDto createCustomerAdmin(CustomerDto customerDto);

    Page<CustomerDto> searchCustomerAdmin(String keyword, Pageable pageable);

    CustomerDtoApi getCustomerById(Long customerId);
}
