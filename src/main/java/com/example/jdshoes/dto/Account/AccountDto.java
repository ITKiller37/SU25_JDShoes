package com.example.jdshoes.dto.Account;


import com.example.jdshoes.dto.AddressShipping.AddressShippingDto;
import com.example.jdshoes.entity.Account;
import lombok.Data;

import java.util.List;

@Data
public class AccountDto {
    private String phoneNumber;
    private String name;
    private String email;
    private String password;
    private List<AddressShippingDto> addressShippingList;
}
