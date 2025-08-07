package com.example.jdshoes.repository;

import com.example.jdshoes.dto.Product.ProductSearchDto;
import com.example.jdshoes.dto.Product.ProductStock;
import com.example.jdshoes.dto.Statistic.BestSellerProduct;
import com.example.jdshoes.dto.Statistic.ProductStatistic;
import com.example.jdshoes.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    WHERE (:keyword is null or p.code like CONCAT('%', :keyword, '%') or p.name like CONCAT('%', :keyword, '%'))
      AND (:nhanHang is null or p.brand.id = :nhanHang)
      AND (:chatLieu is null or p.material.id = :chatLieu)
      AND (:theLoai is null or p.category.id = :theLoai)
      AND (:trangThai is null or p.status = :trangThai)
      AND p.deleteFlag = false
    GROUP BY p.id, p.code, p.name, p.brand.name, p.material.name, p.category.name, p.status, p.createDate
    """)
    Page<ProductSearchDto> listSearchProduct(String keyword, Long nhanHang, Long chatLieu, Long theLoai, Integer trangThai, Pageable pageable);

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
    boolean existsByName(String name);

    Product findTopByOrderByIdDesc();

    Product findByCode(String code);

    @Query(value = "SELECT\n" +
            "    p.id,\n" +
            "    p.code,\n" +
            "    p.name,\n" +
            "    brand.name AS brand,\n" +
            "    cat.name AS category,\n" +
            "    COALESCE(SUM(bd.quantity), 0) AS totalQuantity,\n" +
            "    COALESCE(SUM(bd.exchangeQuantity), 0) AS totalQuantityReturn,\n" +
            "    COALESCE(SUM(bd.quantity * bd.detailPrice), 0) AS revenue\n" +
            "FROM\n" +
            "    product p\n" +
            "JOIN\n" +
            "    productDetail pd ON p.id = pd.productId\n" +
            "LEFT JOIN\n" +
            "    billDetail bd ON pd.id = bd.productDetailId\n" +
            "LEFT JOIN\n" +
            "    bill b ON bd.billId = b.id\n" +
            "LEFT JOIN\n" +
            "    brand ON brand.id = p.brandId \n" +
            "LEFT JOIN\n" +
            "    category cat ON cat.id = p.categoryId\n" +
            "WHERE \n" +
            "    (b.status = 'HOAN_THANH' AND b.createDate BETWEEN ?1 AND ?2 OR b.status IS NULL)\n" +
            "GROUP BY\n" +
            "    p.id, p.code, p.name, brand.name, cat.name;", nativeQuery = true)
    List<ProductStatistic> getStatisticProduct(String fromDate, String toDate);


    @Query(value = "SELECT top(10) p.id, p.code, p.name, \n" +
            "(SELECT top(1) image.link FROM image INNER JOIN ProductDetail pdt on pdt.id = Image.productDetailId WHERE pdt.productId = p.id) as imageUrl, \n" +
            "brand.name as brand, cat.name as category, COALESCE(SUM(bd.quantity),0) AS totalQuantity, \n" +
            "COALESCE(SUM(bd.quantity),0) AS totalQuantityReturn, SUM(bd.quantity * bd.detailPrice) as revenue\n" +
            "from bill b join billDetail bd on b.id = bd.billId join productDetail pd on pd.id = bd.productDetailId \n" +
            "join product p on p.id = pd.productId left join bill_return br on br.bill_id = b.id\n" +
            "join brand on brand.id = p.brandId join category cat on cat.id = p.categoryId\n" +
            "where b.status = 'HOAN_THANH'\n" +
            "group by p.id, p.code, p.name, brand.name, cat.name order by totalQuantity DESC", nativeQuery = true)
    List<BestSellerProduct> getBestSellerProduct();


    @Query(value = "SELECT top(10)  p.id, p.code, p.name, (SELECT top(1) image.link FROM image WHERE image.product_id = p.id) as imageUrl , brand.name as brand, cat.name as category, COALESCE(SUM(bd.quantity),0) AS totalQuantity, COALESCE(SUM(bd.return_quantity),0) AS totalQuantityReturn, SUM(bd.quantity * bd.moment_price) as revenue\n" +
            "             from bill b join billDetail bd on b.id = bd.bill_id join productDetail pd on pd.id = bd.product_detail_id \n" +
            "            join product p on p.id = pd.product_id left join bill_return br on br.bill_id = b.id\n" +
            "            join brand on brand.id = p.brand_id join category cat on cat.id = p.category_id\n" +
            "            where b.status = 'HOAN_THANH' and b.createDate BETWEEN :fromDate AND :toDate \n" +
            "            group by p.id, p.code, p.name, brand.name, cat.name order by totalQuantity DESC", nativeQuery = true)
    List<BestSellerProduct> getBestSellerProduct(String fromDate, String toDate);

    @Query(value = "select p.name, p.code, c.name as categoryName,\n" +
            "(select sum(pd.quantity) from ProductDetail pd where pd.productId = p.id) as stock\n" +
            "from Product p inner join Category c on c.id = p.categoryId", nativeQuery = true)
    List<ProductStock> getProductStock();

}