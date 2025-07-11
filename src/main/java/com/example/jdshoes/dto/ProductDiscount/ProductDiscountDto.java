package com.example.jdshoes.dto.ProductDiscount;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Valid
@Getter
@Setter
public class ProductDiscountDto {

    private Long id;

    private String code;

    private String name;

    private String type;

    private BigDecimal value;
    
    private String status;

    private boolean closed;

    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private BigDecimal discountedAmount;

    private List<Long> productDetailIds;
}
