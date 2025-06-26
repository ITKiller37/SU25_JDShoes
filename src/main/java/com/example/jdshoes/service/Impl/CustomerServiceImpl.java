package com.example.jdshoes.service.Impl;



import com.example.jdshoes.dto.CustomerDto.CustomerDto;
import com.example.jdshoes.entity.Customer;
import com.example.jdshoes.exception.ShoesApiException;
import com.example.jdshoes.repository.CustomerRepository;
import com.example.jdshoes.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.map(this::convertToDto);
    }

    @Override
    public CustomerDto createCustomerAdmin(CustomerDto customerDto) {

        if(customerDto.getCode().trim() == "" || customerDto.getCode() == null) {
            Customer customerCurrent = customerRepository.findTopByOrderByIdDesc();
            Long nextCode = (customerCurrent == null) ? 1 : customerCurrent.getId() + 1;
            String productCode = "KH" + String.format("%04d", nextCode);
            customerDto.setCode(productCode);
        }

        if(customerDto.getCode().trim() != null  ) {
            if(customerRepository.existsByCode(customerDto.getCode())) {
                throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Mã khách hàng đã tồn tại");
            }

        }
        if(customerRepository.existsByPhoneNumber(customerDto.getPhoneNumber())) {
            throw new ShoesApiException(HttpStatus.BAD_REQUEST, "Số điện thoại khách hàng đã tồn tại");

        }
        Customer customer = convertToEntity(customerDto);
        return convertToDto(customerRepository.save(customer));
    }

    @Override
    public Page<CustomerDto> searchCustomerAdmin(String keyword, Pageable pageable) {
        Page<Customer> customerPage = customerRepository.searchCustomerKeyword(keyword, pageable);
        return customerPage.map(this::convertToDto);
    }

    private CustomerDto convertToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setCode(customer.getCode());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        return customerDto;
    }

    private Customer convertToEntity(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setCode(customerDto.getCode());
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setAccount(null);
        customer.setAddressShippings(null);
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        return customer;
    }
}
