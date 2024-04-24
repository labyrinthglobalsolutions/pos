package admin_user.repositories;


import admin_user.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository  extends JpaRepository<Subcategory,Long > {


}
