package admin_user.repositories;


import admin_user.model.PosDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosDetailsRepository extends JpaRepository<PosDetails, Long> {

    List<PosDetails> findByProductIdAndPosId(Long product_id, Long pos_id);

    List<PosDetails> findByOrderId(String order_id);
}
