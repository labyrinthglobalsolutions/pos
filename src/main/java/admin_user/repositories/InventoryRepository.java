package admin_user.repositories;

import admin_user.model.Inventory;
import admin_user.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query(value = "SELECT i FROM Inventory i WHERE i.product_id = :product_id AND i.selling_price = :selling_price")
    List<Inventory> findDataByProductId(@Param("product_id") Product product_id, @Param("selling_price") int selling_price);

    @Query(value = "SELECT i FROM Inventory i WHERE i.productCode = :product_code")
    List<Inventory> findByProductId(@Param("product_code") String product_code);
}
