package com.example.jdshoes.service;



import com.example.jdshoes.dto.AddressShipping.AddressShippingDto;
import com.example.jdshoes.dto.AddressShipping.AddressShippingDtoAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressShippingService {
    List<AddressShippingDto> getAddressShippingByAccountId();
    AddressShippingDto saveAddressShippingUser(AddressShippingDto addressShippingDto);

    AddressShippingDto saveAddressShippingAdmin(AddressShippingDtoAdmin addressShippingDto);

    void deleteAddressShipping(Long id);
}
