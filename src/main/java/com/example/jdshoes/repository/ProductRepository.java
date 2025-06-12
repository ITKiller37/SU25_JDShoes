package com.example.jdshoes.repository;

import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByDeleteFlagFalse(Pageable pageable);

    @Query("select p from Product p join ProductDetail pd on p.id = pd.product.id where pd.id = :productDetailId")
    Product findByProductDetail_Id(Long productDetailId);

    @Query(value = "SELECT p.id as idSanPham,p.code as maSanPham,p.name as tenSanPham,p.brand.name as nhanHang,p.material.name as chatLieu,p.category.name as theLoai,p.status as trangThai FROM Product p where (:maSanPham is null or p.code like CONCAT('%', :maSanPham, '%')) and " +
            "(:tenSanPham is null or p.name like CONCAT('%', :tenSanPham, '%')) and (:nhanHang is null or p.brand.id=:nhanHang) and " +
            "(:chatLieu is null or p.material.id=:chatLieu) and (:theLoai is null or p.category.id=:theLoai) and (:trangThai is null or p.status=:trangThai) and(p.deleteFlag = false)")
    Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable);


    @Query(value = "SELECT p.id as idSanPham,p.code as maSanPham,p.name as tenSanPham,p.brand.name as nhanHang,p.material.name as chatLieu,p.category.name as theLoai,p.status as trangThai FROM Product p where p.deleteFlag = false")
    Page<ProductSearchDto> getAll(Pageable pageable);
}
