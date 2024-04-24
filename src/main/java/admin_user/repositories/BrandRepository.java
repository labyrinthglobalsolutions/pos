package admin_user.repositories;


import admin_user.model.Brand;
import admin_user.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BrandRepository  extends JpaRepository<Brand,Long > {
    List<Brand> findByBrandName(String brandName);

    // Custom query method using @Query annotation with JPQL
    @Query("SELECT c FROM Brand c WHERE c.startDate > ?1")
    List<Brand> findByStartDateAfter(LocalDate startDate);
}
