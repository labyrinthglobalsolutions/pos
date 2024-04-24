package admin_user.repositories;

import admin_user.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByMobileNo(String mobile_no);

    Optional<Customer> findById(Long id);
}
