package com.example.jdshoes.dto.Brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BrandDto {

    private Long id;

    @NotBlank(message = "Mã nhãn hàng không được để trống")
    @NotNull(message = "Mã nhãn hàng không được để trống")
    @NotEmpty(message = "Mã nhãn hàng không được để trống")
    @Size(max = 20, message = "Mã nhãn hàng không được vượt quá 20 ký tự")
    private String code;

    @NotBlank(message = "Tên nhãn hàng không được để trống")
    @NotNull(message = "Tên nhãn hàng không được để trống")
    @Size(max = 50, message = "Tên nhãn hàng không được vượt quá 50 ký tự")
    private String name;
}
