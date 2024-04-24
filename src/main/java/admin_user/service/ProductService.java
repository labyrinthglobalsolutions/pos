package admin_user.service;


import admin_user.model.Category;
import admin_user.model.Product;
import admin_user.model.Subcategory;
import admin_user.repositories.ProductRepository;
import admin_user.service.container.ProductContainer;
import admin_user.service.request.Column;
import admin_user.service.request.OrderCol;
import admin_user.service.request.ServerSideRequest;
import org.apache.commons.collections4.ComparatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;


    public  List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsBySubcategory(Subcategory subcategory) {
        return productRepository.findBySubcategory(subcategory);
    }
    public List<Product> getProductByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }


    public List<Product> getListing1(ServerSideRequest ssr) {
        return productRepository.findAll();
    }

    public ProductContainer getListing(ServerSideRequest ssr) {
        //
        // Just for demo purposes.
        //
        long grandTotal = productRepository.count();
        List<Product> all = productRepository.findAll();

        // interim results to get total rows after filtering:
        List<Product> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Product> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

        return new ProductContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<Product> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Product>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Product> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                       Collator coll) {

        // default sort:
        Comparator<Product> comp = Comparator.comparing(Product::getName, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {

            }
            case "name" ->
                    comp = Comparator.comparing(Product::getName, coll);
            case "id" ->
                    comp = Comparator.comparing(Product::getId, coll);
            case "category" ->
                    comp = Comparator.comparing(Product::getCategory_Name, coll);
            case "subcategory" ->
                    comp = Comparator.comparing(Product::getSubcategory_name, coll);
            case "brand" ->
                    comp = Comparator.comparing(Product::getBrand_name, coll);
            case "description" ->
                    comp = Comparator.comparing(Product::getDescription, coll);
            case "MRP" ->
                    comp = Comparator.comparing(Product::getPrice, coll);
            case "GST%" ->
                    comp = Comparator.comparing(Product::getGst_percent, coll);
            case "Type" ->
                    comp = Comparator.comparing(Product::getGst_type, coll);
            case "Action" -> {
            }
        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Product> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getName() != null && normalize(e.getName().toLowerCase()).contains(searchTerm))
                || (e.getId() != null && normalize(e.getId().toString()).contains(searchTerm));
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
