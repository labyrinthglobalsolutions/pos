package admin_user.repositories;


import admin_user.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Custom query method using method naming convention
    List<Category> findByCategoryName(String categoryName);

    // Custom query method using @Query annotation with JPQL
    @Query("SELECT c FROM Category c WHERE c.startDate > ?1")
    List<Category> findByStartDateAfter(LocalDate startDate);
}
