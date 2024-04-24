package admin_user.service.container;

import admin_user.model.Brand;

import java.util.List;

public record BrandContainer(
        long draw,
        long recordsTotal,
        long recordsFiltered,
        List<Brand> data) {

}
