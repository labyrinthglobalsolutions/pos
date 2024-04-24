package admin_user.service.request;

public record Column(
        String data,
        String name,
        boolean searchable,
        boolean orderable,
        Search search) {

}
