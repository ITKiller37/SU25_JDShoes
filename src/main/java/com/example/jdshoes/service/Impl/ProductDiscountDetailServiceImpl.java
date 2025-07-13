//package com.example.jdshoes.service.Impl;
//
//import com.example.jdshoes.entity.ProductDetail;
//import com.example.jdshoes.entity.ProductDiscount;
//import com.example.jdshoes.entity.ProductDiscountDetail;
//import com.example.jdshoes.repository.ProductDetailRepository;
//import com.example.jdshoes.repository.ProductDiscountDetailRepository;
//import com.example.jdshoes.repository.ProductDiscountRepository;
//import com.example.jdshoes.service.ProductDiscountDetailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ProductDiscountDetailServiceImpl  implements ProductDiscountDetailService {
//
//    @Autowired
//    private ProductDiscountDetailRepository repository;
//
//    @Autowired
//    private ProductDiscountRepository discountRepo;
//
//    @Autowired
//    private ProductDetailRepository detailRepo;
//
//    @Override
//    public void addProductToDiscount(Long discountId, List<Long> productDetailIds) {
//        ProductDiscount discount = discountRepo.findById(Math.toIntExact(discountId)).orElseThrow();
//        for (Long productDetailId : productDetailIds) {
//            ProductDetail detail = detailRepo.findById(productDetailId).orElseThrow();
//            ProductDiscountDetail entity = new ProductDiscountDetail();
//            entity.setProductDiscount(discount);
//            entity.setProductDetail(detail);
//            repository.save(entity);
//        }
//    }
//
//    @Override
//    public void removeProductFromDiscount(Long discountDetailId) {
//        repository.deleteById(discountDetailId);
//    }
//
//    @Override
//    public List<ProductDiscountDetail> findAllByDiscountId(Long discountId) {
//        return repository.findByProductDiscountId(discountId);
//    }
//}
