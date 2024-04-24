package admin_user.service.container;

import admin_user.model.Inventory;


import java.util.List;

public record InventoryContainer(

    long draw,
    long recordsTotal,
    long recordsFiltered,
    List<Inventory> data){

}