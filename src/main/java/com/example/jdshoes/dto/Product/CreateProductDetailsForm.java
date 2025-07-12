package com.example.jdshoes.dto.Product;

import com.example.jdshoes.entity.ProductDetail;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class CreateProductDetailsForm {
    private List<ProductDetail> productDetailList;
}
