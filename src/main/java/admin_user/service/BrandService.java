package admin_user.service;


import admin_user.model.Brand;
import admin_user.model.Product;
import admin_user.repositories.BrandRepository;
import admin_user.service.container.BrandContainer;
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
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    public void saveBrand(Brand brand) {
        brandRepository.save(brand);
    }

    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }


    public BrandContainer getListing(ServerSideRequest ssr) {


        long grandTotal = brandRepository.count();
        List<Brand> all = brandRepository.findAll();

        // interim results to get total rows after filtering:
        List<Brand> filtered = all.stream()
                .filter(containsText(normalize(ssr.search().value().toLowerCase())))
                .toList();

        // one page of results:
        List<Brand> data = filtered.stream()
                .sorted(buildSorter(ssr))
                .skip(ssr.start())
                .limit(ssr.length())
                .toList();

        return new BrandContainer(ssr.draw(), grandTotal, filtered.size(), data);
    }

    private Comparator<Brand> buildSorter(ServerSideRequest ssr) {
        // for multi-column sorting:
        List<Comparator<Brand>> sortChain = new ArrayList<>();
        for (OrderCol oc : ssr.order()) {
            sortChain.add(buildComparatorClause(oc, ssr.columns(), getCollator()));
        }
        // combines list of comparators into one comparator:
        return ComparatorUtils.chainedComparator(sortChain);
    }

    private Comparator<Brand> buildComparatorClause(OrderCol oc, List<Column> columns,
                                                       Collator coll) {

        // default sort:
        Comparator<Brand> comp = Comparator.comparing(Brand::getBrandName, coll);
        // which field to sort on - get column data name from column index:
        String colName = columns.get(oc.column()).data();
        switch (colName) {
            case "sno" -> {

            }
            case "name" ->
                    comp = Comparator.comparing(Brand::getBrandName, coll);
            case "id" ->
                    comp = Comparator.comparing(Brand::getId, coll);
            case "Action" -> {
            }
        }
        // ascending or descending:
        return oc.dir().equals("asc") ? comp : comp.reversed();
    }

    private Predicate<? super Brand> containsText(String searchTerm) {
        // search all fields for case-insensitive search term:
        return e -> (e.getBrandName()!= null && normalize(e.getBrandName().toLowerCase()).contains(searchTerm))
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

    public List<Product> getAllproduct() {
       return getAllproduct();
    }
}

