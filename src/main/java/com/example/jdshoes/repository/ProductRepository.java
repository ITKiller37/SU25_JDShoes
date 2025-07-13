package com.example.jdshoes.repository;

import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    Page<Product> findAllByDeleteFlagFalse(Pageable pageable);

    @Query("select p from Product p join ProductDetail pd on p.id = pd.product.id where pd.id = :productDetailId")
    Product findByProductDetailId(Long productDetailId);

    @Query(value = """
    SELECT p.id as idSanPham,
           p.code as maSanPham,
           p.name as tenSanPham,
           p.brand.name as nhanHang,
           p.material.name as chatLieu,
           p.category.name as theLoai,
           p.status as trangThai,
           SUM(pd.quantity) as totalQuantity
    FROM Product p
    LEFT JOIN ProductDetail pd ON p.id = pd.product.id
    WHERE (:maSanPham is null or p.code like CONCAT('%', :maSanPham, '%'))
      AND (:tenSanPham is null or p.name like CONCAT('%', :tenSanPham, '%'))
      AND (:nhanHang is null or p.brand.id = :nhanHang)
      AND (:chatLieu is null or p.material.id = :chatLieu)
      AND (:theLoai is null or p.category.id = :theLoai)
      AND (:trangThai is null or p.status = :trangThai)
      AND p.deleteFlag = false
    GROUP BY p.id, p.code, p.name, p.brand.name, p.material.name, p.category.name, p.status, p.createDate
    """)
    Page<ProductSearchDto> listSearchProduct(String maSanPham, String tenSanPham, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable);

    @Query(value = """
    SELECT p.id as idSanPham,
           p.code as maSanPham,
           p.name as tenSanPham,
           p.brand.name as nhanHang,
           p.material.name as chatLieu,
           p.category.name as theLoai,
           p.status as trangThai,
           SUM(pd.quantity) as totalQuantity
    FROM Product p
    LEFT JOIN ProductDetail pd ON p.id = pd.product.id
    WHERE p.deleteFlag = false
    GROUP BY p.id, p.code, p.name, p.brand.name, p.material.name, p.category.name, p.status, p.createDate
    """)
    Page<ProductSearchDto> getAll(Pageable pageable);

    boolean existsByCode(String code);

    Product findTopByOrderByIdDesc();

    Product findByCode(String code);

}
