package com.example.jdshoes.dto.Product;



import com.example.jdshoes.entity.ProductDetail;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductDetailsForm {
    private List<ProductDetail> productDetailList;
}
