package admin_user.controller;

import admin_user.model.Category;
import admin_user.model.Pos;
import admin_user.model.PosDetails;
import admin_user.service.PosService;
import admin_user.service.container.InventoryContainer;
import admin_user.service.container.PosContainer;
import admin_user.service.request.ServerSideRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pos")
public class PosController {
    @Autowired
    private PosService posService;

    @GetMapping(path="/create-new-order")
    public @ResponseBody Object createOrder() {
        List<String> orderData = posService.getOrderId();
        String orderId = orderData.get(0);
        String sNo = orderData.get(1);
        String order_status = "PENDING";
        posService.createOrder(orderId, order_status, sNo);
        return orderId;
    }

    @GetMapping(path="/get-pending-orders")
    public @ResponseBody Object pendingOrder() {
        //String orderId = posService.getOrderId();
        String order_status = "PENDING";
        List<Pos> orderIdArr = posService.getPendingOrders(order_status);
        return orderIdArr;
    }

    @PostMapping(path="/add-product-to-order")
    public @ResponseBody String addProductToOrder(PosDetails posDetails) {
        String posDetails1 = posService.addProductToOrder(posDetails);
        return posDetails1;
    }

    @GetMapping(value="/get-order-items/{id}")
    public @ResponseBody Object getOrderItems(@PathVariable String id) {
        List<PosDetails> posDetails = posService.getItemsByOrderId(id);
        return posDetails;
    }

    @GetMapping(value="/get-order-details/{id}")
    public @ResponseBody Object getOrderDetails(@PathVariable String id) {
        List<Pos> pos = posService.getPosDetailsById(id);
        return pos;
    }

    @PostMapping(value="/place-order")
    public @ResponseBody Object placeOrder(@RequestParam("orderStatus") String orderStatus,
                                           @RequestParam("customerName") String customerName,
                                           @RequestParam("mobileNo") String mobileNo,
                                           @RequestParam("gstNo") String gstNo,
                                           @RequestParam("paymentMode") String paymentMode,
                                           @RequestParam("order_id") String order_id) {
        posService.placeOrder(orderStatus, customerName, mobileNo, gstNo, paymentMode, order_id);
        return orderStatus;
    }

    @GetMapping(value="/get-invoice-details/{order_id}")
    public @ResponseBody Object getInvoiceDetails(@PathVariable String order_id) {
        Object pos = posService.getInvoiceDetails(order_id);
        return pos;
    }

    @PostMapping(path="/listing")
    @ResponseBody
    public PosContainer postListing(@RequestBody ServerSideRequest ssr) {
        return posService.getListing(ssr);
    }
}




