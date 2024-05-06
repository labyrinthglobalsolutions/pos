package admin_user.service.container;

import admin_user.model.Pos;

import java.util.List;

public record PosContainer(long draw,
                           long recordsTotal,
                           long recordsFiltered,
                           List<Pos> data) {
}
