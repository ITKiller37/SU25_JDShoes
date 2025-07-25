package com.example.jdshoes.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressDto {
    private String street;
    private String ward;
    private String district;
    private String province;
    private boolean isDefault;
}
