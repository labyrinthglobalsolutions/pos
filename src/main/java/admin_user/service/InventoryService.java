package admin_user.service;

import admin_user.model.Category;
import admin_user.model.Inventory;
import admin_user.model.InventoryMap;
import admin_user.model.Product;
import admin_user.repositories.InventoryRepository;
import admin_user.repositories.InventoryMapRepository;
import admin_user.repositories.ProductRepository;
import admin_user.service.container.InventoryContainer;
import admin_user.service.request.Column;
import admin_user.service.request.OrderCol;
import admin_user.service.request.ServerSideRequest;
import org.apache.commons.collections4.ComparatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.text.Normalizer;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapRepository inventoryMapRepository;

    @Autowired
    private ProductRepository productRepository;

    public static List<Inventory> getAllInventory() {
        return getAllInventory();
    }


    public long addInventory(Inventory inventory) {
        long inventory_id;
        Product product_id = inventory.getProduct_id();
        int selling_price = inventory.getSelling_price();
        List<Inventory> inventories = getInventoryByProductIdAndSellingPrice(product_id, selling_price);
        if(inventories.isEmpty()) {
            Inventory savedInventory = inventoryRepository.save(inventory);
            inventory_id = savedInventory.getId();
        }else{
            Inventory existingInventory = inventories.get(0); // Assuming there's only one matching entry
            int currentQty = existingInventory.getQuentity();
            int newQty = inventory.getQuentity();
            int totalQty = currentQty + newQty;
            existingInventory.setQuentity(totalQty);
            inventoryRepository.save(existingInventory); // Update existing inventory
            inventory_id = existingInventory.getId();
        }
        return inventory_id;
    }

    public void addInventoryMap(long inventory_id, Inventory inventory){
        LocalDate localDate = LocalDate.now(ZoneId.of("GMT+05:30"));
        LocalTime localTime = LocalTime.now(ZoneId.of("GMT+05:30"));
        InventoryMap inventoryMap = new InventoryMap();
        inventoryMap.setInventory_id(inventory_id);
        inventoryMap.setBatch_no(inventory.getBatch_no());
        inventoryMap.setQuentity(inventory.getQuentity());
        inventoryMap.setReceiveing_date(localDate);
        inventoryMap.setReceiveing_time(localTime);

        InventoryMap addInventoryMap=inventoryMapRepository.save(inventoryMap);
    }

    public List<Inventory> getInventoryByProductIdAndSellingPrice(Product product_id, int selling_price){
        return inventoryRepository.findDataByProductId(product_id, selling_price);
    }


    public Product getProductById(Long id) {
        return getProductById(id);
    }

    public List<Category> getAllCategories() {
        return getAllCategories();
    }

    public void saveInventory(Product product) {
    }

    public void deleteInventory(Long id) {
    }

    public Inventory getInventoryById(Long id) {
        return getInventoryById(id);
    }


    public List<Inventory> getListing1(ServerSideRequest ssr) {
        return inventoryRepository.findAll();
    }

    public InventoryContainer getListing(ServerSideRequest ssr) {
        //
        // Just for demo purposes.
        //
        long grandTotal = inventoryRepository.count();
        List<Inventory> all = inventoryRepository.findAll();

        // interim results to get total rows after filtering:
        List<Inventory> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Inventory> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

        return new InventoryContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<Inventory> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Inventory>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Inventory> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                      Collator coll) {

        // default sort:
        Comparator<Inventory> comp = Comparator.comparing(Inventory::getBatch_no, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {

            }
            case "name" ->
                    comp = Comparator.comparing(Inventory::getProduct_id, coll);
            case "batch_no" ->
                    comp = Comparator.comparing(Inventory::getBatch_no, coll);
            case "selling_price" ->
                    comp = Comparator.comparing(Inventory::getSelling_price, coll);
            case "quentity" ->
                    comp = Comparator.comparing(Inventory::getQuentity, coll);

        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Inventory> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getBatch_no() != null && normalize(e.getBatch_no().toLowerCase()).contains(searchTerm))
                || (e.getProduct_id() != null && normalize(e.getProduct_id().toString()).contains(searchTerm));
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
