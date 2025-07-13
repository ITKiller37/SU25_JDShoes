package com.example.jdshoes.dto.Discount;

import lombok.Data;

@Data
public class DiscountStatusDTO {
    private Integer id;
    private int status;

    public DiscountStatusDTO(Integer id, int status) {
        this.id = id;
        this.status = status;
    }
}
