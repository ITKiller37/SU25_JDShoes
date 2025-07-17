package com.example.jdshoes.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    private String code;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
}
