package admin_user.service.container;

import admin_user.model.Subcategory;

import java.util.List;

public record CategoryContainer(long draw,
                                long recordsTotal,
                                long recordsFiltered,
                                List<Subcategory> data) {
}
