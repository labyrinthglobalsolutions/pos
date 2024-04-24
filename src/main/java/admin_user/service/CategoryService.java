package admin_user.service;


import admin_user.model.Category;
import admin_user.service.container.ResponseContainer;
import admin_user.service.request.Column;
import admin_user.service.request.OrderCol;
import admin_user.repositories.CategoryRepository;
import admin_user.service.request.ServerSideRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import org.apache.commons.collections4.ComparatorUtils;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public ResponseContainer getListing(ServerSideRequest ssr) {
        //
        // Just for demo purposes.
        //
        long grandTotal = categoryRepository.count();
        List<Category> all = categoryRepository.findAll();

        // interim results to get total rows after filtering:
        List<Category> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Category> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

        return new ResponseContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<Category> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Category>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Category> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                       Collator coll) {

        // default sort:
        Comparator<Category> comp = Comparator.comparing(Category::getCategoryName, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {

            }
            case "name" ->
                    comp = Comparator.comparing(Category::getCategoryName, coll);


            case "id" ->
                    comp = Comparator.comparing(Category::getId, coll);
            case "Action" -> {
            }
        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Category> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getCategoryName() != null && normalize(e.getCategoryName().toLowerCase()).contains(searchTerm))
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
