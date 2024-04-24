package admin_user.repositories;


import admin_user.model.Brand;
import admin_user.model.Category;
import admin_user.model.Product;
import admin_user.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Long> {


    List<Product> findByCategory(Category category);
    List<Product> findBySubcategory(Subcategory subcategory);
    List<Product> findByBrand(Brand brand);

    @Query(value = "SELECT p FROM Product p WHERE p.productCode = :productCode")
    List<Product> findByProductCode(@Param("productCode") String product_code);

}
