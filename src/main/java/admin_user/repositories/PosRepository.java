package admin_user.repositories;

import admin_user.model.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PosRepository extends JpaRepository<Pos, Long> {
    @Query(value = "SELECT p FROM Pos p ORDER BY p.sNo DESC LIMIT 1")
    List<Pos> findMaxSno();

    List<Pos> findByOrderStatus(String order_status);

    List<Pos> findByOrderId(String order_id);
}
