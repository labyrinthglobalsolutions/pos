package admin_user.service;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

import admin_user.model.*;
import admin_user.repositories.*;
import admin_user.service.container.PosContainer;
import admin_user.service.request.Column;
import admin_user.service.request.OrderCol;
import admin_user.service.request.ServerSideRequest;
import org.apache.commons.collections4.ComparatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosService {
    @Autowired
    private PosRepository posRepository;

    @Autowired
    private PosDetailsRepository posDetailsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<String> getOrderId() {
        long sNo;
        String orderId;
        Date date = new Date();
        String strDateFormat = "ddmmyy";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);

        List<Pos> maxSno = getMaxOrderId();
        if(maxSno.isEmpty() || maxSno.get(0) == null) {
            sNo = 1;
        }else{
            Pos maxSnoArr = maxSno.get(0); // Assuming there's only one matching entry
            sNo = maxSnoArr.getsNo() + 1;
        }
        orderId = "ORD-"+formattedDate+sNo;
        List<String> orderData = new ArrayList<>();
        orderData.add(orderId);
        orderData.add(String.valueOf(sNo));
        return orderData;
    }

    public List<Pos> getMaxOrderId(){
        return posRepository.findMaxSno();
    }

    public List<Pos> getPendingOrders(String order_status)
    {
        List<Pos> pendingOrders= posRepository.findByOrderStatus(order_status);
        return pendingOrders;
    }

    public String createOrder(String order_id, String order_status, String sNo){
        Pos posData = new Pos();
        posData.setOrderId(order_id);
        posData.setOrderStatus(order_status);
        posData.setsNo(Integer.parseInt(sNo));
        posData.setDate(new Date());
        posRepository.save(posData);
        return "Success";
    }

    public Object getInvoiceDetails(String order_id)
    {
        Map<String, String> orderData = new HashMap<String, String>();
        List<Pos> posData = posRepository.findByOrderId(order_id);
        Pos posDetails = posData.get(0);
        Long pos_id = posDetails.getId();
        Long customer_id = posDetails.getCustomerId();
        double grand_total = posDetails.getGrandTotal();
        double total_gst = posDetails.getTotalGst();
        double sub_total = grand_total - total_gst;
        String payment_mode = posDetails.getPaymentMode();

        Optional<Customer> customerData = customerRepository.findById(customer_id);
        Customer customerDetails = customerData.get();
        String customer_name = customerDetails.getCustomerName();
        String customer_mobile_no = customerDetails.getMobileNo();
        String customer_gst_no = customerDetails.getGstNo();

        orderData.put("customer_name", customer_name);
        orderData.put("customer_mobile_no", customer_mobile_no);
        orderData.put("customer_gst_no", customer_gst_no);
        orderData.put("grand_total", String.valueOf(grand_total));
        orderData.put("total_gst", String.valueOf(total_gst));
        orderData.put("sub_total", String.valueOf(sub_total));
        orderData.put("payment_mode", payment_mode);
        return orderData;
    }

    public String addProductToOrder(PosDetails posDetails)
    {
        String product_code = posDetails.getProductCode();
        String order_id = posDetails.getOrderId();

        List<Product> productList = productRepository.findByProductCode(product_code);
        if(productList.isEmpty()){
            return "202";
        }else {
            Product productDetails = productList.get(0);
            Long product_id = productDetails.getId();
            int get_percent = productDetails.getGst_percent();

            List<Inventory> inventoryList = inventoryRepository.findByProductId(product_code);
            if(inventoryList.isEmpty()){
                return "203";
            }else {
                Inventory inventoryDetails = inventoryList.get(0);
                Long inventory_id = inventoryDetails.getId();
                double selling_price = inventoryDetails.getSelling_price();
                int inventory_quantity = inventoryDetails.getQuentity();

                if (inventory_quantity > 0) {
                    List<Pos> postList = posRepository.findByOrderId(order_id);
                    Pos posOverviewDetails = postList.get(0);
                    Long pos_id = posOverviewDetails.getId();

                    double multiply = selling_price * 100;
                    double multiply_gst = 100 + get_percent;
                    double rate = multiply / multiply_gst;
                    double gst_price = selling_price - rate;

                    BigDecimal rate_bd = new BigDecimal(rate);
                    rate_bd = rate_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    rate = rate_bd.doubleValue();

                    BigDecimal gst_price_bd = new BigDecimal(gst_price);
                    gst_price_bd = gst_price_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    gst_price = gst_price_bd.doubleValue();

                    int total_qty;

                    List<PosDetails> postDetailsList = posDetailsRepository.findByProductIdAndPosId(product_id, pos_id);
                    if (postDetailsList.isEmpty()) {
                        total_qty = 1;
                        posDetails.setQty(total_qty);
                        posDetails.setSellingPrice(selling_price);
                        posDetails.setStatus(1);
                        posDetails.setTotalGst(gst_price);
                        posDetails.setTotalRate(rate);
                        posDetails.setPosId(pos_id);
                        posDetails.setProductCode(product_code);
                        posDetails.setProductId(product_id);
                        posDetails.setInventoryId(inventory_id);
                        posDetails.setOrderId(order_id);
                        posDetailsRepository.save(posDetails);
                    } else {
                        PosDetails postDetailsData = postDetailsList.get(0);

                        int current_qty = postDetailsData.getQty();
                        total_qty = current_qty + 1;

                        double current_selling_price = postDetailsData.getSellingPrice();
                        double total_selling_price = current_selling_price + selling_price;

                        double current_rate = postDetailsData.getTotalRate();
                        double total_rate = current_rate + rate;

                        double current_gst = postDetailsData.getTotalGst();
                        double total_gst = current_gst + gst_price;

                        BigDecimal total_rate_bd = new BigDecimal(total_rate);
                        total_rate_bd = total_rate_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        total_rate = total_rate_bd.doubleValue();

                        BigDecimal total_gst_bd = new BigDecimal(total_gst);
                        total_gst_bd = total_gst_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        total_gst = total_gst_bd.doubleValue();

                        postDetailsData.setQty(total_qty);
                        postDetailsData.setSellingPrice(total_selling_price);
                        postDetailsData.setTotalRate(total_rate);
                        postDetailsData.setTotalGst(total_gst);
                        posDetailsRepository.save(postDetailsData);
                    }

                    double current_total_gst_overview = posOverviewDetails.getTotalGst();
                    double current_total_rate_overview = posOverviewDetails.getTotalRate();
                    double current_grand_total_overview = posOverviewDetails.getGrandTotal();

                    double total_gst_overview = current_total_gst_overview + gst_price;
                    double total_rate_overview = current_total_rate_overview + rate;
                    double grand_total_overview = current_grand_total_overview + selling_price;

                    BigDecimal total_rate_overview_bd = new BigDecimal(total_rate_overview);
                    total_rate_overview_bd = total_rate_overview_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    total_rate_overview = total_rate_overview_bd.doubleValue();

                    BigDecimal total_gst_overview_bd = new BigDecimal(total_gst_overview);
                    total_gst_overview_bd = total_gst_overview_bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    total_gst_overview = total_gst_overview_bd.doubleValue();

                    posOverviewDetails.setTotalGst(total_gst_overview);
                    posOverviewDetails.setTotalRate(total_rate_overview);
                    posOverviewDetails.setGrandTotal(grand_total_overview);
                    posRepository.save(posOverviewDetails);

                    int remaining_qty = inventory_quantity - 1;
                    inventoryDetails.setQuentity(remaining_qty);
                    inventoryRepository.save(inventoryDetails);
                    return order_id;
                } else {
                    return "201";
                }
            }
        }
    }

    public List<PosDetails> getItemsByOrderId(String order_id)
    {
        List<PosDetails> posDetails = posDetailsRepository.findByOrderId(order_id);
        return posDetails;
    }

    public List<Pos> getPosDetailsById(String order_id)
    {
        List<Pos> pos = posRepository.findByOrderId(order_id);
        return pos;
    }

    public void placeOrder(String orderStatus, String customerName, String mobileNo, String gstNo, String paymentMode, String order_id)
    {
        Long customer_id;
        List<Pos> postList = posRepository.findByOrderId(order_id);
        Pos posOverviewDetails = postList.get(0);

        List<Customer> customerList = customerRepository.findByMobileNo(mobileNo);

        if(!customerList.isEmpty()){
            Customer customerDetails = customerList.get(0);
            customer_id = customerDetails.getId();
        }else{
            Customer customer = new Customer();
            customer.setCustomerName(customerName);
            customer.setMobileNo(mobileNo);
            customer.setGstNo(gstNo);
            Customer addedCustomer = customerRepository.save(customer);
            customer_id = addedCustomer.getId();
        }

        posOverviewDetails.setOrderStatus(orderStatus);
        posOverviewDetails.setCustomerId(customer_id);
        posOverviewDetails.setPaymentMode(paymentMode);
        posRepository.save(posOverviewDetails);
    }




    public PosContainer getListing(ServerSideRequest ssr) {
        //
        // Just for demo purposes.
        //
        long grandTotal = posRepository.count();
        List<Pos> all = posRepository.findAll();

        // interim results to get total rows after filtering:
        List<Pos> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Pos> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

        return new PosContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<? super Pos> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Pos>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Pos> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                        Collator coll) {

        // default sort:
        Comparator<Pos> comp = Comparator.comparing(Pos::getOrderId, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {
            }
            case "id" ->
                    comp = Comparator.comparing(Pos::getId, coll);
            case "order_id" ->
                    comp = Comparator.comparing(Pos::getOrderId, coll);
            case "customer_name" ->
                    comp = Comparator.comparing(Pos::getCustomerId, coll);
            case "grand_total" ->
                    comp = Comparator.comparing(Pos::getGrandTotal, coll);
        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Pos> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getOrderId() != null && normalize(e.getOrderId().toLowerCase()).contains(searchTerm))
                || (e.getOrderId() != null && normalize(e.getOrderId().toString()).contains(searchTerm));
    }

    private Collator getCollator() {
        Collator coll = Collator.getInstance(Locale.US);
        coll.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        return coll;
    }

    private String normalize(String input) {
        if (input == null) {
            return null;
        }
        // NFD = canonical decomposition; \p{Mn} = non-spacing marks:
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{Mn}", "");
    }





}
