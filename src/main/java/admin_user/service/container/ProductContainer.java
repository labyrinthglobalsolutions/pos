package admin_user.service.container;

import admin_user.model.Product;

import java.util.List;
public record ProductContainer(

    long draw,
    long recordsTotal,
    long recordsFiltered,
    List<Product> data) {
}
