package com.example.jdshoes.dto.AddressShipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressShippingDto {
    private Long id;
    private String address;

    private Boolean isDefault = false;

}
