package admin_user.service;


import admin_user.model.Subcategory;
import admin_user.repositories.SubCategoryRepository;
import admin_user.service.container.SubcategoryContainer;
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
public class SubCategoryService {

    @Autowired
    private SubCategoryRepository subcategoryRepository;

    public List<Subcategory> getAllSubcategories() {
        return subcategoryRepository.findAll();
    }

    public Subcategory getSubcategoryById(Long id) {
        return subcategoryRepository.findById(id).orElse(null);
    }

    public void saveSubcategory(Subcategory subcategory) {
        subcategoryRepository.save(subcategory);
    }

    public void deleteSubcategory(Long id) {
        subcategoryRepository.deleteById(id);
    }


    public SubcategoryContainer getListing(ServerSideRequest ssr) {
        long grandTotal = subcategoryRepository.count();
        List<Subcategory> all = subcategoryRepository.findAll();

        // interim results to get total rows after filtering:
        List<Subcategory> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Subcategory> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

            return new SubcategoryContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<Subcategory> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Subcategory>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Subcategory> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                          Collator coll) {

        // default sort:
        Comparator<Subcategory> comp = Comparator.comparing(Subcategory::getSubcategory_name, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {

            }
            case "name" ->
                    comp = Comparator.comparing(Subcategory::getSubcategory_name, coll);


            case "id" ->
                    comp = Comparator.comparing(Subcategory::getId, coll);
            case "Action" -> {
            }
        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Subcategory> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getSubcategory_name() != null && normalize(e.getSubcategory_name().toLowerCase()).contains(searchTerm))
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




