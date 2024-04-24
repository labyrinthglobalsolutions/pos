package admin_user.service.container;

import java.util.List;

public record ResponseContainer(
        long draw,
        long recordsTotal,
        long recordsFiltered,
        List<admin_user.model.Category> data) {

}
