package com.example.jdshoes.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDtoApi {
    private String code;
    private String name;
    private String phoneNumber;
    private String email;
    private String gender;
    private Date birthDay;
    private List<AddressDto> address; // Thay thế String address bằng List AddressDto
}

